package com.honeymorning.api.context.infra.database;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public interface RedisContext {
	int REDIS_PORT = 6379;

	String SPRING_DATA_REDIS_HOST = "spring.data.redis.host";
	String SPRING_DATA_REDIS_PORT = "spring.data.redis.port";

	@Container
	GenericContainer<?> redisContainer = new GenericContainer<>("redis:latest")
		.withExposedPorts(REDIS_PORT);

	@DynamicPropertySource
	static void registerRedisProperties(DynamicPropertyRegistry registry) {
		registry.add(SPRING_DATA_REDIS_HOST, redisContainer::getHost);
		registry.add(SPRING_DATA_REDIS_PORT, () -> redisContainer.getMappedPort(6379)
			.toString());
	}
}
