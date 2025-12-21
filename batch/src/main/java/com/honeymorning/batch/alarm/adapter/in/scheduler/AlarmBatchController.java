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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honeymorning.batch.utils.TimeUtils;
import com.honeymorning.common.domain.alarm.entity.DayOfTheWeek;

import lombok.extern.slf4j.Slf4j;

@Profile({"local", "prod"})
@Slf4j
@RequestMapping("/api/batch/alarm")
@RestController
public class AlarmBatchController {

	private final JobLauncher asyncJobLauncher;
	private final Job alarmToAlarmEventCreateJob;

	@Autowired
	public AlarmBatchController(JobLauncher asyncJobLauncher, Job alarmToAlarmEventCreateJob) {
		this.asyncJobLauncher = asyncJobLauncher;
		this.alarmToAlarmEventCreateJob = alarmToAlarmEventCreateJob;
	}

	@PostMapping
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
			.addLong("createdAt", LocalDate.now().toEpochDay())
			.addLocalDate("startAt", LocalDate.now())
			.addLong("today", (long)today)
			.addLocalTime("startTime", startTime)
			.addLocalTime("endTime", endTime)
			.toJobParameters()
		);
	}
}
