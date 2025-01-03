package com.sf.honeymorning.context.infra.database;

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

	String DATABASE_NAME = "honeymorning";
	String DATABASE_USERNAME = "test_honeymorning";
	String DATABASE_PASSWORD = "wldkwhdkkiskkj";

	String DATASOURCE_URL = "spring.datasource.url";
	String DATASOURCE_USERNAME = "spring.datasource.username";
	String DATASOURCE_PASSWORD = "spring.datasource.password";

	@Container
	MySQLContainer<?> mysqlContainer = new MySQLContainer<>(VERSION)
		.withDatabaseName(DATABASE_NAME)
		.withUsername(DATABASE_USERNAME)
		.withPassword(DATABASE_PASSWORD)
		.withReuse(true);

	@DynamicPropertySource
	static void setDataSourceProperties(DynamicPropertyRegistry registry) {
		registry.add(DATASOURCE_URL, mysqlContainer::getJdbcUrl);
		registry.add(DATASOURCE_USERNAME, mysqlContainer::getUsername);
		registry.add(DATASOURCE_PASSWORD, mysqlContainer::getPassword);
	}
}
