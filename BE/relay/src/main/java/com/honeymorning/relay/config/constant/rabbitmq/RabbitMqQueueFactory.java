package com.honeymorning.relay.config.constant.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqQueueFactory {

	Queue mainQueue(String base) {
		return QueueBuilder.durable(RabbitNamingGenerator.queue(base))
			.withArgument("x-max-length", 100_000)
			.withArgument("x-dead-letter-exchange", RabbitNamingGenerator.exchange(base))
			.withArgument("x-dead-letter-routing-key", RabbitNamingGenerator.routingKey(base))
			.withArgument("x-overflow", "reject-publish")
			.quorum()
			.build();
	}

	Queue dlq(String base) {
		return QueueBuilder.durable(RabbitNamingGenerator.dlq(base))
			.quorum()
			.build();
	}

	DirectExchange mainExchange(String base) {
		return new DirectExchange(RabbitNamingGenerator.exchange(base));
	}

	DirectExchange dlqExchange(String base) {
		return new DirectExchange(RabbitNamingGenerator.exchange(RabbitNamingGenerator.dlq(base)));
	}

	Binding mainBinding(String base, Queue queue, DirectExchange exchange) {
		return BindingBuilder.bind(queue)
			.to(exchange)
			.with(RabbitNamingGenerator.routingKey(base));
	}

	Binding dlqBinding(String base, Queue dlq, DirectExchange exchange) {
		return BindingBuilder.bind(dlq)
			.to(exchange)
			.with(RabbitNamingGenerator.routingKey(RabbitNamingGenerator.dlq(base)));
	}
}

