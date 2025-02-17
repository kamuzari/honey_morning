package com.sf.honeymorning.context.infra.broker;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public interface KafkaContext {

	@Container
	KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.1"));
	String SPRING_KAFKA_BOOTSTRAP_SERVERS = "spring.kafka.bootstrap-servers";

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {
		kafka.start();
		registry.add(SPRING_KAFKA_BOOTSTRAP_SERVERS, kafka::getBootstrapServers);
	}
}
