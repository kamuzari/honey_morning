package com.honeymorning.api.context.integration;

import org.springframework.boot.test.context.SpringBootTest;

import com.honeymorning.api.context.infra.database.MySqlContext;
import com.honeymorning.api.context.infra.database.RedisContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public non-sealed class EndPointIntegrationTest extends CommonIntegrationContext
	implements MySqlContext, RedisContext {
}
