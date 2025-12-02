package com.honeymorning.relay.context.infra.broker;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;

public interface RabbitMqContext {

	Integer[] PORTS = {5672, 15672};
	String USERNAME = "admin";
	String PASSWORD = "admin";

	String RABBITMQ_HOST = "spring.rabbitmq.host";
	String RABBITMQ_PORT = "spring.rabbitmq.port";
	String RABBITMQ_USERNAME = "spring.rabbitmq.username";
	String RABBITMQ_PASSWORD = "spring.rabbitmq.password";

	RabbitMQContainer rabbitMqContainer = createRabbitMq();

	private static RabbitMQContainer createRabbitMq() {
		RabbitMQContainer container = new RabbitMQContainer("rabbitmq:management")
			.withExposedPorts(PORTS)
			.withUser(USERNAME, PASSWORD);
		container.start();

		return container;
	}

	@DynamicPropertySource
	static void setRabbitMqProperties(DynamicPropertyRegistry registry) {
		registry.add(RABBITMQ_HOST, rabbitMqContainer::getHost);
		registry.add(RABBITMQ_PORT, () -> rabbitMqContainer.getMappedPort(PORTS[0]));
		registry.add(RABBITMQ_USERNAME, rabbitMqContainer::getAdminUsername);
		registry.add(RABBITMQ_PASSWORD, rabbitMqContainer::getAdminPassword);
	}
}