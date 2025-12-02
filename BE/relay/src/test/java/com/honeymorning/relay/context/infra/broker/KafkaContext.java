package com.honeymorning.relay.context.infra.broker;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public interface KafkaContext {
	String SPRING_KAFKA_BOOTSTRAP_SERVERS = "spring.kafka.bootstrap-servers";

	KafkaContainer kafka = createKafka();

	static KafkaContainer createKafka() {
		KafkaContainer container = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.1"));
		container.start();

		return container;
	}

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {
		registry.add(SPRING_KAFKA_BOOTSTRAP_SERVERS, kafka::getBootstrapServers);
	}
}