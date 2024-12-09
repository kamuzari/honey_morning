package com.sf.honeymorning.alarm.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.honeymorning.alarm.service.dto.ToAIRequestDto;
import com.sf.honeymorning.config.constant.QueueProperties;
import com.sf.honeymorning.context.ServiceIntegrationTest;

@Testcontainers
class AiClientServiceTest extends ServiceIntegrationTest {

	@Container
	static RabbitMQContainer rabbitMqContainer = new RabbitMQContainer("rabbitmq:management")
		.withExposedPorts(5672, 15672)
		.withUser("admin", "admin")
		.withReuse(true);

	@DynamicPropertySource
	static void setRabbitMqProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.rabbitmq.host", rabbitMqContainer::getHost);
		registry.add("spring.rabbitmq.port", () -> rabbitMqContainer.getMappedPort(5672));
		registry.add("spring.rabbitmq.username", rabbitMqContainer::getAdminUsername);
		registry.add("spring.rabbitmq.password", rabbitMqContainer::getAdminPassword);
	}

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Autowired
	AiClientService aiClientService;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	ConnectionFactory connectionFactory;

	@Test
	@DisplayName("AI 큐에 메시지를 전달하다")
	void testServeMessage() {
		//given
		Long userId = 1L;
		List<String> userTags = List.of("정치");
		aiClientService.deliver(new ToAIRequestDto(userId, userTags));

		//when
		Object response = rabbitTemplate.receiveAndConvert(QueueProperties.AI_PROPERTY.getQueueName());
		ToAIRequestDto requestDto = objectMapper.convertValue(response, new TypeReference<ToAIRequestDto>() {
		});

		// then
		assertThat(requestDto).isNotNull();
		assertThat(requestDto.userId()).isEqualTo(userId);
		assertThat(requestDto.tags()).containsAll(userTags);
	}


	@Test
	@DisplayName("존재하지 않는 Queue로 메시지를 보내면 반환된다")
	void testDeliverMessageFail() throws InterruptedException {
		// given
		String exchange = "";
		String routingKey = "non.existent.queue";
		String messageContent = "test";
		String noRouteMessage = "NO_ROUTE";

		RabbitTemplate subRabbitTemplate = setTemplate();

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
		subRabbitTemplate.convertAndSend(exchange, routingKey, messageContent);

		// then
		boolean awaitResult = latch.await(1, TimeUnit.SECONDS);
		assertThat(awaitResult).isTrue();
		assertThat(responseMessage.toString()).contains(noRouteMessage);
		assertThat(responseMessage.toString()).contains(routingKey);
		assertThat(responseMessage.toString()).contains(messageContent);
	}

	private RabbitTemplate setTemplate() {
		RabbitTemplate subRabbitTemplate = new RabbitTemplate(connectionFactory);
		subRabbitTemplate.setMandatory(true);

		return subRabbitTemplate;
	}

}