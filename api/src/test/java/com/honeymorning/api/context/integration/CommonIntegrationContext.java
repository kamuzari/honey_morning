package com.honeymorning.api.context.integration;

import org.testcontainers.junit.jupiter.Testcontainers;

import net.datafaker.Faker;

@Testcontainers
public sealed class CommonIntegrationContext permits DefaultIntegrationTest, EndPointIntegrationTest {
	protected Long authId = new Faker().number().randomNumber();
}
