package com.honeymorning.batch.alarm.adapter.in.scheduler;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.honeymorning.batch.utils.TimeUtils;
import com.honeymorning.common.domain.alarm.entity.DayOfTheWeek;

import lombok.extern.slf4j.Slf4j;

@Profile({"local", "prod"})
@Slf4j
@Component
public class AlarmBatchScheduler {
	private static final String CRON_PER_MINUTE = "0 0/1 * * * *";

	private final JobLauncher asyncJobLauncher;
	private final Job alarmToAlarmEventCreateJob;

	@Autowired
	public AlarmBatchScheduler(JobLauncher asyncJobLauncher, Job alarmToAlarmEventCreateJob) {
		this.asyncJobLauncher = asyncJobLauncher;
		this.alarmToAlarmEventCreateJob = alarmToAlarmEventCreateJob;
	}

	@Scheduled(cron = CRON_PER_MINUTE)
	public void scheduleAlarmJob() throws
		JobInstanceAlreadyCompleteException,
		JobExecutionAlreadyRunningException,
		JobParametersInvalidException,
		JobRestartException
	{
		LocalTime startTime = TimeUtils.getNow().plusMinutes(40);
		LocalTime endTime = startTime.plusMinutes(1).minusSeconds(1);
		Integer today = DayOfTheWeek.getToday();
		log.info("start batch job start time -> {}, end time -> {}, today -> {}", startTime, endTime, today);

		asyncJobLauncher.run(alarmToAlarmEventCreateJob, new JobParametersBuilder()
			.addLocalDate("startAt", LocalDate.now())
			.addLong("today", (long)today)
			.addLocalTime("startTime", startTime)
			.addLocalTime("endTime", endTime)
			.toJobParameters()
		);

	}
}
