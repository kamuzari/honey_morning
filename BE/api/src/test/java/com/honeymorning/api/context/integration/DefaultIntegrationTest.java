package com.honeymorning.api.context.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.honeymorning.api.context.infra.database.MySqlContext;
import com.honeymorning.api.context.infra.database.RedisContext;

@Transactional
@SpringBootTest
public non-sealed class DefaultIntegrationTest extends CommonIntegrationContext
	implements MySqlContext, RedisContext {
}
