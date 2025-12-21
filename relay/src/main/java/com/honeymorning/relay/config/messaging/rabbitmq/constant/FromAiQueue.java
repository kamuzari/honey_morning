package com.honeymorning.relay.config.messaging.rabbitmq.constant;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FromAiQueue implements QueueDeclaration {
	public static final String NAME = "ai.generated.alarm_contents_response";

	private final RabbitMqQueueFactory factory;

	public FromAiQueue(RabbitMqQueueFactory factory) {
		this.factory = factory;
	}

	@Bean
	public Queue fromAiQueueDeclare() {
		return factory.mainQueue(NAME);
	}

	@Bean
	public DirectExchange fromAiExchange() {
		return factory.mainExchange(NAME);
	}

	@Bean
	public Binding fromAiBinding(Queue fromAiQueueDeclare, DirectExchange fromAiExchange) {
		return factory.mainBinding(NAME, fromAiQueueDeclare, fromAiExchange);
	}

	@Bean
	public Queue fromAiDlq() {
		return factory.dlq(NAME);
	}

	@Bean
	public DirectExchange fromAiDlqExchange() {
		return factory.dlqExchange(NAME);
	}

	@Bean
	public Binding fromAiDlqBinding(Queue fromAiDlq, DirectExchange fromAiDlqExchange) {
		return factory.dlqBinding(NAME, fromAiDlq, fromAiDlqExchange);
	}

	@Override
	public String getQueueName() {
		return NAME;
	}

	@Override
	public String getExchangeName() {
		return RabbitNamingGenerator.exchange(NAME);
	}

	@Override
	public String getRoutingKey() {
		return RabbitNamingGenerator.routingKey(NAME);
	}

	@Override
	public String getDeadQueueName() {
		return RabbitNamingGenerator.dlq(NAME);
	}

	@Override
	public String getDeadExchangeName() {
		return RabbitNamingGenerator.exchange(RabbitNamingGenerator.dlq(NAME));
	}

	@Override
	public String getDeadRoutingKey() {
		return RabbitNamingGenerator.routingKey(RabbitNamingGenerator.dlq(NAME));
	}
}
