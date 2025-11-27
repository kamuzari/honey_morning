package com.honeymorning.relay.config.database;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;

@EnableJpaRepositories(
	basePackages = "com.honeymorning.relay.event.repository",
	entityManagerFactoryRef = "eventEntityManagerFactory",
	transactionManagerRef = "eventTransactionManager"
)
@Configuration
public class EventDataSourceConfig {

	@Bean(name = "eventDataSourceProperties")
	@ConfigurationProperties("spring.datasource.event")
	public DataSourceProperties dataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean(name = "eventDataSource")
	@ConfigurationProperties("spring.datasource.event.hikari")
	public DataSource dataSource(DataSourceProperties eventDataSourceProperties) {
		return eventDataSourceProperties.initializeDataSourceBuilder()
			.type(HikariDataSource.class)
			.build();
	}

	@Bean(name = "eventEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
		EntityManagerFactoryBuilder builder,
		@Qualifier("eventDataSource") DataSource dataSource) {
		return builder.dataSource(dataSource)
			.packages("com.honeymorning.relay.event.entity")
			.persistenceUnit("<<eventDataSource>>")
			.build();
	}

	@Bean(name = "eventTransactionManager")
	public PlatformTransactionManager transactionManager(
		@Qualifier("eventEntityManagerFactory") EntityManagerFactory emf) {
		return new JpaTransactionManager(emf);
	}
}