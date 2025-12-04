package com.honeymorning.batch.alarm.application.step.partition;

import static com.honeymorning.batch.alarm.application.constant.AlarmBatchConstant.STEP_MANAGER;
import static com.honeymorning.batch.alarm.application.constant.AlarmBatchConstant.STEP_PARTITIONER;
import static com.honeymorning.batch.alarm.application.constant.AlarmBatchConstant.THREAD_PREFIX;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.honeymorning.batch.common.step.ModularPartitioner;

@Configuration
public class AlarmContentPartition {
	private final JobRepository jobRepository;
	private final int batchThreadPoolSize;

	public AlarmContentPartition(
		JobRepository jobRepository,
		@Value("${batch.alarm.thread-pool-size:10}") Integer batchThreadPoolSize) {

		this.jobRepository = jobRepository;
		this.batchThreadPoolSize = batchThreadPoolSize;
	}

	public TaskExecutor executor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(batchThreadPoolSize);
		executor.setMaxPoolSize(batchThreadPoolSize);
		executor.setThreadNamePrefix(THREAD_PREFIX);
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
	public Step alarmsStepManager(
		ModularPartitioner partitioner,
		Step alarmStep,
		TaskExecutorPartitionHandler partitionHandler) {

		return new StepBuilder(STEP_MANAGER, jobRepository)
			.partitioner(STEP_PARTITIONER, partitioner)
			.step(alarmStep)
			.partitionHandler(partitionHandler)
			.build();
	}
}
