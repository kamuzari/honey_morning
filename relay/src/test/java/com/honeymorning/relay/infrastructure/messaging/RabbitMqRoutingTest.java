package com.honeymorning.relay.infrastructure.messaging;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.honeymorning.relay.config.messaging.rabbitmq.constant.FromAiQueue;
import com.honeymorning.relay.context.integration.DefaultIntegrationTest;

class RabbitMqRoutingTest extends DefaultIntegrationTest {
	@Autowired
	RabbitTemplate rabbitTemplate;

	@Autowired
	FromAiQueue fromAiQueue;

	@Autowired
	ConnectionFactory connectionFactory;

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

	private RabbitTemplate createRabbitTemplate() {
		RabbitTemplate subRabbitTemplate = new RabbitTemplate(connectionFactory);
		subRabbitTemplate.setMandatory(true);

		return subRabbitTemplate;
	}

}
