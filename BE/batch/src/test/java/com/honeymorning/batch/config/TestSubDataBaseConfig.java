package com.honeymorning.batch.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@TestConfiguration
public class TestSubDataBaseConfig {
	private static final String SUB_DB_SQL_SCRIPT = "./init.sql";
	private final ResourceLoader resourceLoader;

	public TestSubDataBaseConfig(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Bean
	public DataSourceInitializer conditionalSubInitializer(
		@Qualifier("alarmContentReadDataSource") DataSource subDataSource) {
		DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(subDataSource);
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(resourceLoader.getResource(SUB_DB_SQL_SCRIPT));
		populator.setContinueOnError(false);
		initializer.setDatabasePopulator(populator);

		return initializer;
	}
}
