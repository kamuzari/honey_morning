package com.honeymorning.api.context.integration;

import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public sealed class CommonIntegrationContext permits DefaultIntegrationTest, EndPointIntegrationTest {

}
