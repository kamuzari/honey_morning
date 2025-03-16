package com.sf.honeymorning.alarm.batch.scheduler;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sf.honeymorning.alarm.domain.entity.DayOfTheWeek;
import com.sf.honeymorning.common.exception.alarm.ReadyAlramBatchException;
import com.sf.honeymorning.common.exception.model.constant.ErrorProtocol;
import com.sf.honeymorning.util.TimeUtils;

import lombok.extern.slf4j.Slf4j;

@Profile({"local", "prod"})
@Slf4j
@Component
public class AlarmBatchScheduler {
	private static final String CRON_PER_MINUTE = "0 0/1 * * * *";

	private final JobLauncher jobLauncher;
	private final Job alarmJob;

	@Autowired
	public AlarmBatchScheduler(JobLauncher jobLauncher, Job alarmJob) {
		this.jobLauncher = jobLauncher;
		this.alarmJob = alarmJob;
	}

	@Scheduled(cron = CRON_PER_MINUTE)
	public void scheduleAlarmJob() {
		try {
			LocalTime startTime = TimeUtils.getNow().plusMinutes(40);
			LocalTime endTime = startTime.plusMinutes(1).minusSeconds(1);
			Integer today = DayOfTheWeek.getToday();
			log.info("start batch job start time -> {}, end time -> {}, today -> {}", startTime, endTime, today);

			jobLauncher.run(alarmJob, new JobParametersBuilder()
				.addLocalDate("startAt", LocalDate.now())
				.addLong("today", (long)today)
				.addLocalTime("startTime", startTime)
				.addLocalTime("endTime", endTime)
				.toJobParameters()
			);

		} catch (Exception e) {
			throw new ReadyAlramBatchException(
				MessageFormat.format("[긴급] - 배치 처리 중 문제가 발생하였습니다. {0}", e),
				e.getCause(),
				ErrorProtocol.UNEXPECTED_FATAL_ERROR);
		}
	}
}
