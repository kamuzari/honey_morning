package com.sf.honeymorning.alarm.batch.scheduler;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.sf.honeymorning.alarm.batch.outbox.EventStatus;
import com.sf.honeymorning.alarm.batch.outbox.OutBoxAlarmEventRepository;
import com.sf.honeymorning.alarm.domain.entity.DayOfTheWeek;
import com.sf.honeymorning.common.exception.alarm.ReadyAlramBatchException;
import com.sf.honeymorning.common.exception.model.ErrorProtocol;
import com.sf.honeymorning.config.RabbitConfig;
import com.sf.honeymorning.util.TimeUtils;

import lombok.extern.slf4j.Slf4j;

@Profile({"local", "prod"})
@Slf4j
@Component
public class AlarmBatchScheduler {
	private static final String PUBLISH_QUEUE_NAME = RabbitConfig.AI_GENERATIVE_ALARM_CONTENTS_QUEUE_NAME;

	private static final String CRON_PER_SECONDS = "0/1 * * * * *";
	private static final String CRON_PER_MINUTE = "0 0/1 * * * *";

	private final JobLauncher jobLauncher;
	private final Job alarmJob;
	private final RabbitTemplate rabbitTemplate;
	private final OutBoxAlarmEventRepository outBoxAlarmEventRepository;

	@Autowired
	public AlarmBatchScheduler(JobLauncher jobLauncher,
		Job alarmJob,
		RabbitTemplate rabbitTemplate,
		OutBoxAlarmEventRepository outBoxAlarmEventRepository) {

		this.jobLauncher = jobLauncher;
		this.alarmJob = alarmJob;
		this.rabbitTemplate = rabbitTemplate;
		this.outBoxAlarmEventRepository = outBoxAlarmEventRepository;
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

	@Scheduled(cron = CRON_PER_SECONDS)
	@Transactional
	public void relayAlarmEvent() {
		outBoxAlarmEventRepository.findTopByEventStatus(EventStatus.PENDING)
			.ifPresent(outBoxAlarmEvent -> {
					outBoxAlarmEvent.update(EventStatus.PUBLISH);

					TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
						@Override
						public void afterCommit() {
							log.info("after commit, publish outBoxAlarmEvent : {}", outBoxAlarmEvent);
							rabbitTemplate.convertAndSend(PUBLISH_QUEUE_NAME, outBoxAlarmEvent.getPayload());
						}

						@Override
						public void afterCompletion(int status) {
							boolean isFail = status != TransactionSynchronization.STATUS_COMMITTED;
							if (isFail) {
								log.error("Transaction failed, event was not sent : {}", outBoxAlarmEvent);
							}
						}
					});
				}
			);
	}

}
