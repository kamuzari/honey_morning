package com.honeymorning.batch.alarm.application.job;

import static com.honeymorning.batch.alarm.application.constant.AlarmBatchConstant.JOB;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.honeymorning.batch.common.step.LoggingJobListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class AlarmContentJob {
	private final JobRepository jobRepository;

	public AlarmContentJob(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}

	@Bean
	public Job alarmToAlarmEventCreateJob(Step alarmsStepManager) {
		return new JobBuilder(JOB, jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(alarmsStepManager)
			.listener(new LoggingJobListener())
			.build();
	}
}
