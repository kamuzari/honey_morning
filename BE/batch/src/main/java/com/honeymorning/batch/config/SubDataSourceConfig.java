package com.honeymorning.batch.config;

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
	basePackages = "com.honeymorning.utils",
	entityManagerFactoryRef = "subEntityManagerFactory",
	transactionManagerRef = "subTransactionManager"
)
@Configuration
public class SubDataSourceConfig {
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.read")
	public DataSourceProperties readDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean(name = "alarmContentReadDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.read.hikari")
	public DataSource alarmContentReadDataSource(DataSourceProperties readDataSourceProperties) {
		return readDataSourceProperties.initializeDataSourceBuilder()
			.type(HikariDataSource.class)
			.build();
	}

	@Bean(name = "subEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean subEntityManagerFactory(
		EntityManagerFactoryBuilder builder,
		@Qualifier("alarmContentReadDataSource") DataSource subDataSource) {

		return builder.dataSource(subDataSource)
			.packages("com.honeymorning.utils")
			.persistenceUnit("<<sub: main service readOnly>>")
			.build();
	}

	@Bean(name = "alarmReadTransactionManager")
	public PlatformTransactionManager alarmReadTransactionManager(
		@Qualifier("subEntityManagerFactory") EntityManagerFactory emf) {
		return new JpaTransactionManager(emf);
	}

}
