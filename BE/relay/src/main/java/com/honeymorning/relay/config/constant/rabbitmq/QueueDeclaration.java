package com.honeymorning.relay.config.constant.rabbitmq;

public interface QueueDeclaration {
	String getQueueName();
	String getExchangeName();
	String getRoutingKey();

	String getDeadQueueName();
	String getDeadExchangeName();
	String getDeadRoutingKey();
}
