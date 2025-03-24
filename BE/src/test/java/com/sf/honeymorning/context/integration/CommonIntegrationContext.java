package com.sf.honeymorning.context.integration;

import com.github.javafaker.Faker;

public sealed class CommonIntegrationContext permits DefaultIntegrationTest, EndPointIntegrationTest {
	protected static final Faker DATE_GENERATOR = new Faker();
}
