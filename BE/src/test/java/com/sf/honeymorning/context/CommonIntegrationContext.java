package com.sf.honeymorning.context;

import com.github.javafaker.Faker;

public sealed class CommonIntegrationContext permits ServiceIntegrationTest, EndPointIntegrationEnvironment {
	protected static final Faker FAKE_DATA_FACTORY = new Faker();
}
