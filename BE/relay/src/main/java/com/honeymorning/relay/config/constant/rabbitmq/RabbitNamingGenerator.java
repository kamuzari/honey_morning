package com.honeymorning.relay.config.constant.rabbitmq;

public final class RabbitNamingGenerator {
	static String queue(String name) {
		return name;
	}

	static String dlq(String name) {
		return name + ".dlq";
	}

	static String exchange(String name) {
		return name + ".exchange";
	}

	static String routingKey(String name) {
		return name + ".key";
	}
}

