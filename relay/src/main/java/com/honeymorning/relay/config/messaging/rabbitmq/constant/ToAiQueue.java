package com.honeymorning.relay.config.messaging.rabbitmq.constant;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToAiQueue implements QueueDeclaration {
	public static final String NAME = "ai.generative.alarm_contents";

	private final RabbitMqQueueFactory factory;

	public ToAiQueue(RabbitMqQueueFactory factory) {
		this.factory = factory;
	}

	@Bean
	public Queue toAiQueueDeclare() {
		return factory.mainQueue(NAME);
	}

	@Bean
	public DirectExchange toAiExchange() {
		return factory.mainExchange(NAME);
	}

	@Bean
	public Binding toAiBinding(Queue toAiQueueDeclare, DirectExchange toAiExchange) {
		return factory.mainBinding(NAME, toAiQueueDeclare, toAiExchange);
	}

	@Bean
	public Queue toAiDlq() {
		return factory.dlq(NAME);
	}

	@Bean
	public DirectExchange toAiDlqExchange() {
		return factory.dlqExchange(NAME);
	}

	@Bean
	public Binding toAiDlqBinding(Queue toAiDlq, DirectExchange toAiDlqExchange) {
		return factory.dlqBinding(NAME, toAiDlq, toAiDlqExchange);
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
