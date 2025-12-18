package com.honeymorning.batch.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import com.honeymorning.batch.config.TestSubDataBaseConfig;
import com.honeymorning.batch.context.DefaultIntegrationContext;
import com.honeymorning.batch.utils.TimeUtils;
import com.honeymorning.common.domain.alarm.entity.AlarmEntity;
import com.honeymorning.common.domain.alarm.entity.AlarmTagEntity;
import com.honeymorning.common.domain.alarm.entity.DayOfTheWeek;
import com.honeymorning.common.domain.alarm.entity.TagEntity;
import com.honeymorning.common.domain.alarm.repository.AlarmRepository;
import com.honeymorning.common.domain.alarm.repository.AlarmTagRepository;
import com.honeymorning.common.domain.alarm.repository.TagRepository;
import com.honeymorning.common.domain.event.entity.OutBoxAlarmEventEntity;
import com.honeymorning.common.domain.event.repository.OutBoxAlarmEventRepository;

@Import(TestSubDataBaseConfig.class)
@SpringBatchTest
@TestPropertySource(properties = {
	"batch.alarm.chunk-size=50",
	"batch.alarm.thread-pool-size=4"
})
class IntegrationAlarmBatchContext extends DefaultIntegrationContext {
	static final int EXPECTED_BATCH_TOTAL_DATA_SIZE = 5;

	@Autowired
	JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	OutBoxAlarmEventRepository outBoxAlarmEventRepository;

	@Autowired
	TagRepository tagRepository;

	@Autowired
	AlarmTagRepository alarmTagRepository;

	@Autowired
	AlarmRepository alarmRepository;

	@Value("${batch.alarm.thread-pool-size}")
	private int poolSize;

	@AfterEach
	void tearDown() {
		outBoxAlarmEventRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("준비된 알람데이터 기반으로 outbox 데이터를 만들어낸다")
	void testBatchProcess() throws Exception {
		//given
		LocalTime startTime = TimeUtils.getNow().plusMinutes(40);
		LocalTime endTime = startTime.plusMinutes(1).minusSeconds(1);
		createAlarmContents(startTime, EXPECTED_BATCH_TOTAL_DATA_SIZE);

		//when
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(new JobParametersBuilder()
			.addLong("today", (long)DayOfTheWeek.getToday())
			.addLocalTime("startTime", startTime)
			.addLocalTime("endTime", endTime)
			.toJobParameters()
		);

		//then
		await().atMost(30, TimeUnit.SECONDS)
			.until(
				() -> jobExecution.getStatus().isUnsuccessful() || jobExecution.getStatus() == BatchStatus.COMPLETED
			);

		assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
		List<OutBoxAlarmEventEntity> result = outBoxAlarmEventRepository.findAll();
		assertThat(result).hasSize(EXPECTED_BATCH_TOTAL_DATA_SIZE);

	}

	@Test
	@DisplayName("job parameter 값이 똑같은게 들어온다면 오류를 발생시켜 재 실행을 하지 않는다.")
	void failExecuteDuplicateJobParameterJob() throws Exception {
		//given
		LocalTime startTime = LocalTime.now().plusMinutes(40);
		LocalTime endTime = startTime.plusMinutes(1).minusSeconds(1);
		JobExecution alreadyJobExecution = jobLauncherTestUtils.launchJob(createAlarmJob(startTime, endTime));
		await().atMost(30, TimeUnit.SECONDS)
			.until(
				() -> alreadyJobExecution.getStatus() == BatchStatus.COMPLETED
			);

		//when
		//then
		assertThatThrownBy(() -> jobLauncherTestUtils.launchJob(createAlarmJob(startTime, endTime)))
			.isInstanceOf(JobInstanceAlreadyCompleteException.class);
	}

	public StepExecution getStepExecution() {
		LocalTime startTime = TimeUtils.getNow().plusMinutes(40);
		LocalTime endTime = startTime.plusMinutes(1).minusSeconds(1);
		createAlarmContents(startTime, EXPECTED_BATCH_TOTAL_DATA_SIZE);

		ExecutionContext context = new ExecutionContext();
		context.putInt("partition", 1);
		context.putInt("modular", poolSize);

		return MetaDataInstanceFactory.createStepExecution(createAlarmJob(startTime, endTime), context);
	}

	private JobParameters createAlarmJob(LocalTime startTime, LocalTime endTime) {
		return new JobParametersBuilder()
			.addLong("today", (long)DayOfTheWeek.getToday())
			.addLocalTime("startTime", startTime)
			.addLocalTime("endTime", endTime)
			.toJobParameters();
	}

	private void createAlarmContents(LocalTime wakeupTime, int size) {
		LongStream.rangeClosed(1, size).forEach((userId) -> {
			TagEntity economy = tagRepository.save(new TagEntity("경제"));
			TagEntity society = tagRepository.save(new TagEntity("사회"));

			AlarmEntity alarmEntity = alarmRepository.save(AlarmEntity.initialize(userId));
			alarmEntity.update(wakeupTime, DayOfTheWeek.getToday(), 1, 1, true);
			alarmRepository.save(alarmEntity);

			alarmTagRepository.save(new AlarmTagEntity(alarmEntity, society));
			alarmTagRepository.save(new AlarmTagEntity(alarmEntity, economy));
		});
	}
}
