package com.honeymorning.api.context.integration;

import org.testcontainers.junit.jupiter.Testcontainers;

import com.github.javafaker.Faker;

@Testcontainers
public sealed class CommonIntegrationContext permits DefaultIntegrationTest, EndPointIntegrationTest {
	protected Long authId = Faker.instance().number().randomNumber();
}
