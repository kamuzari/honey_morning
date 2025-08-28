package com.honeymorning.api.context.infra.database;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public interface MySqlContext {
	String VERSION = "mysql:8.0";

	String PRIMARY_DATABASE_NAME = "honeymorning";
	String PRIMARY_DATABASE_USERNAME = "test_honeymorning";
	String PRIMARY_DATABASE_PASSWORD = "wldkwhdkkiskkj";

	String PRIMARY_DATASOURCE_URL = "spring.datasource.primary.url";
	String PRIMARY_DATASOURCE_USERNAME = "spring.datasource.primary.username";
	String PRIMARY_DATASOURCE_PASSWORD = "spring.datasource.primary.password";

	String EVENT_DATABASE_NAME = "evet_honeymorning";
	String EVENT_DATABASE_USERNAME = "test_honeymorning";
	String EVENT_DATABASE_PASSWORD = "wldkwhdkkiskkj";

	String EVENT_DATASOURCE_URL = "spring.datasource.event.url";
	String EVENT_DATASOURCE_USERNAME = "spring.datasource.event.username";
	String EVENT_DATASOURCE_PASSWORD = "spring.datasource.event.password";

	@Container
	MySQLContainer<?> primaryDb = new MySQLContainer<>(VERSION)
		.withDatabaseName(PRIMARY_DATABASE_NAME)
		.withUsername(PRIMARY_DATABASE_USERNAME)
		.withPassword(PRIMARY_DATABASE_PASSWORD)
		.withReuse(true);

	@Container
	MySQLContainer<?> eventDb = new MySQLContainer<>(VERSION)
		.withDatabaseName(EVENT_DATABASE_NAME)
		.withUsername(EVENT_DATABASE_USERNAME)
		.withPassword(EVENT_DATABASE_PASSWORD)
		.withReuse(true);

	@DynamicPropertySource
	static void setDataSourceProperties(DynamicPropertyRegistry registry) {
		registry.add(PRIMARY_DATASOURCE_URL, primaryDb::getJdbcUrl);
		registry.add(PRIMARY_DATASOURCE_USERNAME, primaryDb::getUsername);
		registry.add(PRIMARY_DATASOURCE_PASSWORD, primaryDb::getPassword);

		registry.add(EVENT_DATASOURCE_URL, eventDb::getJdbcUrl);
		registry.add(EVENT_DATASOURCE_USERNAME, eventDb::getUsername);
		registry.add(EVENT_DATASOURCE_PASSWORD, eventDb::getPassword);
	}
}
