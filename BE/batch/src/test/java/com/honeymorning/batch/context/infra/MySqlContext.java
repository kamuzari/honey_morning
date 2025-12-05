package com.honeymorning.batch.context.infra;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public interface MySqlContext {
	String VERSION = "mysql:8.0";

	String PRIMARY_DATABASE_NAME = "honeymorning";
	String PRIMARY_DATABASE_USERNAME = "test_honeymorning";
	String PRIMARY_DATABASE_PASSWORD = "test";

	String PRIMARY_DATASOURCE_URL = "spring.datasource.primary.url";
	String PRIMARY_DATASOURCE_USERNAME = "spring.datasource.primary.username";
	String PRIMARY_DATASOURCE_PASSWORD = "spring.datasource.primary.password";

	String SUB_DATABASE_NAME = "sub_honeymorning";
	String SUB_DATABASE_USERNAME = "sub_honeymorning";
	String SUB_DATABASE_PASSWORD = "test";

	String SUB_DATASOURCE_URL = "spring.datasource.read.url";
	String SUB_DATASOURCE_USERNAME = "spring.datasource.read.username";
	String SUB_DATASOURCE_PASSWORD = "spring.datasource.read.password";

	@Container
	MySQLContainer<?> primaryDb = new MySQLContainer<>(VERSION)
		.withDatabaseName(PRIMARY_DATABASE_NAME)
		.withUsername(PRIMARY_DATABASE_USERNAME)
		.withPassword(PRIMARY_DATABASE_PASSWORD)
		.withEnv("TZ", "Asia/Seoul")
		.withReuse(true);

	@Container
	MySQLContainer<?> subDb = new MySQLContainer<>(VERSION)
		.withDatabaseName(SUB_DATABASE_NAME)
		.withUsername(SUB_DATABASE_USERNAME)
		.withPassword(SUB_DATABASE_PASSWORD)
		.withEnv("TZ", "Asia/Seoul")
		.withReuse(true);

	@DynamicPropertySource
	static void setDataSourceProperties(DynamicPropertyRegistry registry) {
		registry.add(PRIMARY_DATASOURCE_URL, primaryDb::getJdbcUrl);
		registry.add(PRIMARY_DATASOURCE_USERNAME, primaryDb::getUsername);
		registry.add(PRIMARY_DATASOURCE_PASSWORD, primaryDb::getPassword);

		registry.add(SUB_DATASOURCE_URL, subDb::getJdbcUrl);
		registry.add(SUB_DATASOURCE_USERNAME, subDb::getUsername);
		registry.add(SUB_DATASOURCE_PASSWORD, subDb::getPassword);
	}
}
