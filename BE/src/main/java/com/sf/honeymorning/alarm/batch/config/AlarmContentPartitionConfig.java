package com.sf.honeymorning.alarm.batch.config;

import static com.sf.honeymorning.alarm.batch.config.AlarmContentJobConfig.ALARM_TO_ALARM_EVENT_CREATE_JOB;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.sf.honeymorning.alarm.batch.step.ModularPartitioner;

@Configuration
public class AlarmContentPartitionConfig {
	private final JobRepository jobRepository;
	private final int batchThreadPoolSize;

	public AlarmContentPartitionConfig(
		JobRepository jobRepository,
		@Value("${batch.alarm.thread-pool-size:10}") Integer batchThreadPoolSize) {

		this.jobRepository = jobRepository;
		this.batchThreadPoolSize = batchThreadPoolSize;
	}

	public TaskExecutor executor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(batchThreadPoolSize);
		executor.setMaxPoolSize(batchThreadPoolSize);
		executor.setThreadNamePrefix(String.join(" -- ", "multi-thread", ALARM_TO_ALARM_EVENT_CREATE_JOB));
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.initialize();

		return executor;
	}

	@Bean
	public TaskExecutorPartitionHandler partitionHandler(Step alarmStep) {
		TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
		partitionHandler.setStep(alarmStep);
		partitionHandler.setTaskExecutor(executor());
		partitionHandler.setGridSize(batchThreadPoolSize);
		return partitionHandler;
	}

	@Bean
	public ModularPartitioner customPartitioner() {
		return new ModularPartitioner();
	}

	@Bean
	public Step stepManager(
		ModularPartitioner partitioner,
		Step alarmStep,
		TaskExecutorPartitionHandler partitionHandler) {

		return new StepBuilder("alarmStep.manager", jobRepository)
			.partitioner("alarmStep", partitioner)
			.step(alarmStep)
			.partitionHandler(partitionHandler)
			.build();
	}
}
