package com.honeymorning.batch.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.io.ResourceLoader;

@TestConfiguration
public class TestSubDataBaseConfig {
	private static final String SUB_DB_SQL_SCRIPT = "./init.sql";
	private final ResourceLoader resourceLoader;

	public TestSubDataBaseConfig(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
}
