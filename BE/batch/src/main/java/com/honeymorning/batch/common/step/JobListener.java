package com.honeymorning.batch.common.step;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class JobListener {

	@Bean
	public JobExecutionListener commonJobListener() {
		return new JobExecutionListener() {
			@Override
			public void afterJob(JobExecution jobExecution) {
				if (jobExecution.getStatus() == BatchStatus.FAILED) {
					log.error("Job execution failed execution: {}", jobExecution);
				}
			}
		};
	}
}
