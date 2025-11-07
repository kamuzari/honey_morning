package com.honeymorning.relay.config.constant.rabbitmq;

import static com.honeymorning.relay.config.constant.rabbitmq.RabbitNamingGenerator.exchange;
import static com.honeymorning.relay.config.constant.rabbitmq.RabbitNamingGenerator.queue;
import static com.honeymorning.relay.config.constant.rabbitmq.RabbitNamingGenerator.routingKey;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqQueueFactory {

	Queue mainQueue(String base) {
		return QueueBuilder.durable(queue(base))
			.withArgument("x-max-length", 100_000)
			.withArgument("x-dead-letter-exchange", exchange(RabbitNamingGenerator.dlq(base)))
			.withArgument("x-dead-letter-routing-key", routingKey(RabbitNamingGenerator.dlq(base)))
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
		return new DirectExchange(exchange(base));
	}

	DirectExchange dlqExchange(String base) {
		return new DirectExchange(exchange(RabbitNamingGenerator.dlq(base)));
	}

	Binding mainBinding(String base, Queue queue, DirectExchange exchange) {
		return BindingBuilder.bind(queue)
			.to(exchange)
			.with(routingKey(base));
	}

	Binding dlqBinding(String base, Queue dlq, DirectExchange exchange) {
		return BindingBuilder.bind(dlq)
			.to(exchange)
			.with(routingKey(RabbitNamingGenerator.dlq(base)));
	}
}

