package com.honeymorning.batch.common.step;

import java.time.Duration;
import java.util.Objects;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class LoggingJobListener implements JobExecutionListener {

	@Override
	public void beforeJob(JobExecution jobExecution) {
		log.info("[JOB 시작] {} - executionId={}",
			jobExecution.getJobInstance().getJobName(),
			jobExecution.getId()
		);
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		long duration = Duration.between(
			Objects.requireNonNull(jobExecution.getStartTime()),
			Objects.requireNonNull(jobExecution.getEndTime())
		).toMillis();

		long totalRead = jobExecution.getStepExecutions().stream()
			.mapToLong(StepExecution::getReadCount)
			.sum();
		long totalWrite = jobExecution.getStepExecutions().stream()
			.mapToLong(StepExecution::getWriteCount)
			.sum();

		log.info("[JOB 종료] {} - status={}, readCount={}, writeCount={}, duration={}ms",
			jobExecution.getJobInstance().getJobName(),
			jobExecution.getStatus(),
			totalRead,
			totalWrite,
			duration
		);

	}
}
