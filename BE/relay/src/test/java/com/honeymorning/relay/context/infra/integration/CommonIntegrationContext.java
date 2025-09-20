package com.honeymorning.relay.context.infra.integration;

import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
public sealed class CommonIntegrationContext permits DefaultIntegrationTest, EndPointIntegrationTest {

}
