package com.honeymorning.batch.alarm.application.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlarmContentJob {
	public static final String ALARM_TO_ALARM_EVENT_CREATE_JOB = "alarmToAlarmEventCreateJob";

	private final JobRepository jobRepository;

	public AlarmContentJob(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}

	@Bean
	public Job alarmToAlarmEventCreateJob(Step stepManager) {
		return new JobBuilder(ALARM_TO_ALARM_EVENT_CREATE_JOB, jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(stepManager)
			.build();
	}

}
