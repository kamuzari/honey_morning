package com.honeymorning.batch.config.framework.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class BatchConfig {

	@Bean
	public BatchDataSourceScriptDatabaseInitializer batchDataSourceInitializer(
		@Qualifier("primaryDataSource") DataSource dataSource,
		BatchProperties properties) {
		return new BatchDataSourceScriptDatabaseInitializer(dataSource, properties.getJdbc());
	}

	@Bean
	@Primary
	public JobLauncher asyncJobLauncher(JobRepository jobRepository) throws Exception {
		TaskExecutorJobLauncher launcher = new TaskExecutorJobLauncher();
		launcher.setJobRepository(jobRepository);

		ThreadPoolTaskExecutor executor = configureThread(6, 10, "BATCH-JOB-");

		launcher.setTaskExecutor(executor);
		launcher.afterPropertiesSet();

		return launcher;
	}

	private ThreadPoolTaskExecutor configureThread(int corePoolSize, int maxPoolSize, String prefix) {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setThreadNamePrefix(prefix);
		executor.initialize();

		return executor;
	}
}
