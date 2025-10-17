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
	basePackages = "com.honeymorning.common.domain.*.repository",
	entityManagerFactoryRef = "readOnlyEntityManagerFactory",
	transactionManagerRef = "readOnlyTransactionManager"
)
@Configuration
public class ReadDataSourceConfig {
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.read")
	public DataSourceProperties readDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean(name = "mainReadOnlyDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.read.hikari")
	public DataSource mainReadOnlyDataSource(DataSourceProperties readDataSourceProperties) {
		return readDataSourceProperties.initializeDataSourceBuilder()
			.type(HikariDataSource.class)
			.build();
	}

	@Bean(name = "readOnlyEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean readOnlyEntityManagerFactory(
		EntityManagerFactoryBuilder builder,
		@Qualifier("mainReadOnlyDataSource") DataSource subDataSource) {

		return builder.dataSource(subDataSource)
			.packages("com.honeymorning.common.domain.*.entity")
			.persistenceUnit("<<readings: main service readOnly>>")
			.build();
	}

	@Bean(name = "readOnlyTransactionManager")
	public PlatformTransactionManager readTransactionManager(
		@Qualifier("readOnlyEntityManagerFactory") EntityManagerFactory emf) {
		return new JpaTransactionManager(emf);
	}

}
