package com.honeymorning.relay.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;

@EnableJpaRepositories(
	basePackages = {"com.honeymorning.common.domain.*.repository"},
	entityManagerFactoryRef = "primaryEntityManagerFactory",
	transactionManagerRef = "primaryTransactionManager"
)
@Configuration
public class PrimaryDataSourceConfig {
	@Bean(name = "primaryDataSourceProperties")
	@ConfigurationProperties("spring.datasource.primary")
	public DataSourceProperties dataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean(name = "primaryDataSource")
	@Primary
	@ConfigurationProperties("spring.datasource.primary.hikari")
	public DataSource dataSource(DataSourceProperties primaryDataSourceProperties) {
		return primaryDataSourceProperties
			.initializeDataSourceBuilder()
			.type(HikariDataSource.class)
			.build();
	}

	@Bean(name = "primaryEntityManagerFactory")
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
		EntityManagerFactoryBuilder builder,
		@Qualifier("primaryDataSource") DataSource dataSource) {

		return builder.dataSource(dataSource)
			.packages("com.honeymorning.common.domain.*.entity")
			.build();
	}

	@Bean(name = "primaryTransactionManager")
	@Primary
	public PlatformTransactionManager transactionManager(
		@Qualifier("primaryEntityManagerFactory") EntityManagerFactory emf) {

		return new JpaTransactionManager(emf);
	}
}
