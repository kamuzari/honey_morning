package com.honeymorning.batch.context;

import org.springframework.boot.test.context.SpringBootTest;

import com.honeymorning.batch.context.infra.MySqlContext;

@SpringBootTest
public class DefaultIntegrationContext implements MySqlContext {
}
