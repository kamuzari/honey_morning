package com.sf.honeymorning.alarm.service;

import static com.sf.honeymorning.brief.entity.violation.TopicWordViolation.SECTION_MAXIMUM_SIZE;
import static com.sf.honeymorning.brief.entity.violation.TopicWordViolation.SECTION_MINIMUM_SIZE;
import static com.sf.honeymorning.brief.entity.violation.TopicWordViolation.TOPIC_WORD_TOTAL_SIZE;
import static com.sf.honeymorning.config.RabbitConfig.AI_GENERATED_ALARM_CONTENTS_RESPONSE_QUEUE_NAME;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.honeymorning.alarm.service.client.AiClientService;
import com.sf.honeymorning.alarm.service.dto.response.AiBriefingDto;
import com.sf.honeymorning.alarm.service.dto.response.AiQuizDto;
import com.sf.honeymorning.alarm.service.dto.response.AiResponseDto;
import com.sf.honeymorning.alarm.service.dto.response.AiTopicDto;
import com.sf.honeymorning.brief.entity.violation.QuizViolation;
import com.sf.honeymorning.context.DefaultIntegrationTest;
import com.sf.honeymorning.context.infra.broker.RabbitMqContext;

class AiClientServiceTest extends DefaultIntegrationTest implements RabbitMqContext {

	@Autowired
	RabbitTemplate rabbitTemplate;

	@SpyBean
	AiClientService aiClientService;

	@Autowired
	ObjectMapper objectMapper;

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
			new AiBriefingDto(FAKE_DATA_FACTORY.lorem().sentence(10), FAKE_DATA_FACTORY.lorem().sentence(40)),
			createFakeQuizDtos(QuizViolation.TOTAL_OF_COUNT),
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
				verify(aiClientService, times(1)).createAlarmContents(expectResponseDto);
			});
	}

	private List<AiTopicDto> createFakeAiTopicDtos(int size) {
		return Stream.generate(() -> new AiTopicDto(
				FAKE_DATA_FACTORY.number().numberBetween(SECTION_MINIMUM_SIZE, SECTION_MAXIMUM_SIZE),
				FAKE_DATA_FACTORY.lorem().word(),
				FAKE_DATA_FACTORY.number().randomDouble(2, 0, 100)))
			.limit(size).toList();
	}

	private List<AiQuizDto> createFakeQuizDtos(int size) {
		return Stream.generate(() -> new AiQuizDto(
				FAKE_DATA_FACTORY.lorem().sentence(2),
				1,
				Stream.generate(() -> FAKE_DATA_FACTORY.lorem().word())
					.limit(QuizViolation.NUMBER_OF_SELECTION)
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