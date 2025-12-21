package com.honeymorning.relay.config.messaging.rabbitmq.constant;

public interface QueueDeclaration {
	String getQueueName();

	String getExchangeName();

	String getRoutingKey();

	String getDeadQueueName();

	String getDeadExchangeName();

	String getDeadRoutingKey();
}
