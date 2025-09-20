package com.honeymorning.relay;

import static com.honeymorning.common.domain.briefing.constraint.TopicWordConstraint.SECTION_MAXIMUM_SIZE;
import static com.honeymorning.common.domain.briefing.constraint.TopicWordConstraint.SECTION_MINIMUM_SIZE;
import static com.honeymorning.common.domain.briefing.constraint.TopicWordConstraint.TOPIC_WORD_TOTAL_SIZE;
import static com.honeymorning.relay.BriefingMockGenerator.GENERATOR;
import static com.honeymorning.relay.config.RabbitConfig.AI_GENERATED_ALARM_CONTENTS_RESPONSE_QUEUE_NAME;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.IllegalTransactionStateException;

import com.honeymorning.common.domain.briefing.constraint.QuizConstraint;
import com.honeymorning.relay.briefing.adapter.in.consumer.AiClientConsumer;
import com.honeymorning.relay.briefing.application.service.AlarmContentService;
import com.honeymorning.relay.briefing.application.service.dto.AiBriefingDto;
import com.honeymorning.relay.briefing.application.service.dto.AiQuizDto;
import com.honeymorning.relay.briefing.application.service.dto.AiResponseDto;
import com.honeymorning.relay.briefing.application.service.dto.AiTopicDto;
import com.honeymorning.relay.context.infra.broker.RabbitMqContext;
import com.honeymorning.relay.context.infra.integration.DefaultIntegrationTest;
import com.rabbitmq.client.Channel;


class AiClientConsumerTest extends DefaultIntegrationTest implements RabbitMqContext {

	@Autowired
	RabbitTemplate rabbitTemplate;

	@SpyBean
	AiClientConsumer aiClientConsumer;

	@Autowired
	ConnectionFactory connectionFactory;

	@SpyBean
	AlarmContentService alarmContentService;

	@Test
	@DisplayName("존재하지 않는 Queue로 메시지를 보내면 반환된다")
	void testDeliverMessageFail() throws InterruptedException {
		// given
		String routingKey = "non.existent.queue";
		String messageContent = "test";
		String noRouteMessage = "NO_ROUTE";

		RabbitTemplate subRabbitTemplate = createRabbitTemplate();
		CountDownLatch latch = new CountDownLatch(1);
		StringBuilder responseMessage = new StringBuilder();

		subRabbitTemplate.setReturnsCallback(returnedMessage -> {
			responseMessage.append("Message: ").append(new String(returnedMessage.getMessage().getBody()))
				.append(", Reply Text: ").append(returnedMessage.getReplyText())
				.append(", Exchange: ").append(returnedMessage.getExchange())
				.append(", Routing Key: ").append(returnedMessage.getRoutingKey());
			latch.countDown();
		});

		// when
		subRabbitTemplate.convertAndSend("", routingKey, messageContent);
		boolean awaitResult = latch.await(1, SECONDS);

		// then
		assertThat(awaitResult).isTrue();
		assertThat(responseMessage.toString()).contains(noRouteMessage);
		assertThat(responseMessage.toString()).contains(routingKey);
		assertThat(responseMessage.toString()).contains(messageContent);
	}

	@Test
	@DisplayName("AI 서버에서 만들어진 알람 컨텐츠 결과를 소비한다")
	void testConsume() {
		//given
		AiResponseDto expectResponseDto = new AiResponseDto(
			1L,
			new AiBriefingDto(GENERATOR.lorem().sentence(10), GENERATOR.lorem().sentence(40)),
			createFakeQuizDtos(QuizConstraint.TOTAL_QUIZ_SIZE),
			createFakeAiTopicDtos(TOPIC_WORD_TOTAL_SIZE),
			List.of("정치"),
			"https://cdn.ycloud.com/03jidmmk39d"
		);

		doNothing().when(alarmContentService).create(expectResponseDto);
		rabbitTemplate.convertAndSend("", AI_GENERATED_ALARM_CONTENTS_RESPONSE_QUEUE_NAME,
			expectResponseDto);

		//when
		//then
		await().atMost(5, SECONDS)
			.untilAsserted(() -> {
				ArgumentCaptor<Channel> channelCaptor = ArgumentCaptor.forClass(Channel.class);
				ArgumentCaptor<Long> tagCaptor = ArgumentCaptor.forClass(Long.class);
				verify(aiClientConsumer, times(1)).createAlarmContents(eq(expectResponseDto), channelCaptor.capture(),
					tagCaptor.capture());
			});
	}

	@Test
	@DisplayName("alarmContentService.create()에서 예외 발생 시 basicNack 이 호출되어 지정한 dlq로 메시지가 이동한다")
	void failConsumeAiResponseProcess() throws Exception {
		// given
		AiResponseDto responseDto = new AiResponseDto(
			1L,
			new AiBriefingDto(GENERATOR.lorem().sentence(10), GENERATOR.lorem().sentence(40)),
			createFakeQuizDtos(QuizConstraint.TOTAL_QUIZ_SIZE),
			createFakeAiTopicDtos(TOPIC_WORD_TOTAL_SIZE),
			List.of("정치"),
			"https://cdn.ycloud.com/03jidmmk39d"
		);

		doThrow(new IllegalTransactionStateException("트랜잭션 예외")).when(alarmContentService).create(responseDto);

		// when
		rabbitTemplate.convertAndSend("", AiClientConsumer.SUBSCRIBE_QUEUE_NAME, responseDto);

		// then
		Message expectedMessageOnDeadLetterQueue = rabbitTemplate.receive("ai.generated.alarm_contents_response.dlq",
			5000);
		Message failedMessage = rabbitTemplate.receive(AiClientConsumer.SUBSCRIBE_QUEUE_NAME, 5000);
		assertThat(expectedMessageOnDeadLetterQueue).isNotNull();
		assertThat(failedMessage).isNull();
	}

	private List<AiTopicDto> createFakeAiTopicDtos(int size) {
		return Stream.generate(() -> new AiTopicDto(
				GENERATOR.number().numberBetween(SECTION_MINIMUM_SIZE, SECTION_MAXIMUM_SIZE),
				GENERATOR.lorem().word(),
				GENERATOR.number().randomDouble(2, 0, 100)))
			.limit(size).toList();
	}

	private List<AiQuizDto> createFakeQuizDtos(int size) {
		return Stream.generate(() -> new AiQuizDto(
				GENERATOR.lorem().sentence(2),
				1,
				Stream.generate(() -> GENERATOR.lorem().word())
					.limit(QuizConstraint.OPTION_SIZE)
					.toList()
			))
			.limit(size)
			.toList();
	}

	private RabbitTemplate createRabbitTemplate() {
		RabbitTemplate subRabbitTemplate = new RabbitTemplate(connectionFactory);
		subRabbitTemplate.setMandatory(true);

		return subRabbitTemplate;
	}

}