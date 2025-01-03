package com.sf.honeymorning.alarm.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;
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
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.sf.honeymorning.alarm.batch.item.dto.ReadyAlarmDto;
import com.sf.honeymorning.alarm.batch.outbox.OutBoxAlarmEvent;
import com.sf.honeymorning.alarm.batch.outbox.OutBoxAlarmEventRepository;
import com.sf.honeymorning.alarm.domain.entity.Alarm;
import com.sf.honeymorning.alarm.domain.entity.AlarmTag;
import com.sf.honeymorning.alarm.domain.entity.DayOfTheWeek;
import com.sf.honeymorning.alarm.domain.repository.AlarmRepository;
import com.sf.honeymorning.alarm.domain.repository.AlarmTagRepository;
import com.sf.honeymorning.context.DefaultIntegrationTest;
import com.sf.honeymorning.context.infra.database.MySqlContext;
import com.sf.honeymorning.tag.entity.Tag;
import com.sf.honeymorning.tag.repository.TagRepository;
import com.sf.honeymorning.util.TimeUtils;

@SpringBatchTest
public class IntegrationAlarmBatchTest extends DefaultIntegrationTest implements MySqlContext {
	static final int EXPECTED_BATCH_TOTAL_DATA_SIZE = 14;

	@Autowired
	JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	JdbcPagingItemReader<ReadyAlarmDto> reader;

	@Autowired
	OutBoxAlarmEventRepository outBoxAlarmEventRepository;

	@Autowired
	AlarmRepository alarmRepository;

	@Autowired
	TagRepository tagRepository;

	@Autowired
	AlarmTagRepository alarmTagRepository;

	@AfterEach
	public void tearDown() {
		outBoxAlarmEventRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("준비된 알람이 총 14개이면, 14개의 outbox model 데이터를 저장한다")
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
		assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
		List<OutBoxAlarmEvent> result = outBoxAlarmEventRepository.findAll();
		assertThat(result).hasSize(EXPECTED_BATCH_TOTAL_DATA_SIZE);
	}

	@Test
	@DisplayName("job parameter 값이 똑같은게 들어온다면 오류를 발생시켜 재 실행을 하지 않는다.")
	void failExecuteDuplicateJobParameterJob() throws Exception {
		//given
		LocalTime startTime = LocalTime.now().plusMinutes(40);
		LocalTime endTime = startTime.plusMinutes(1).minusSeconds(1);

		//when
		//then
		jobLauncherTestUtils.launchJob(createAlarmJob(startTime, endTime));
		assertThatThrownBy(() -> jobLauncherTestUtils.launchJob(createAlarmJob(startTime, endTime)))
			.isInstanceOf(JobInstanceAlreadyCompleteException.class);
	}

	@Test
	@DisplayName("reader 10개 단위로 데이터를 읽어오고, 총 14개의 데이터만 읽는다 ")
	void testReader() throws Exception {
		//given
		//when
		reader.open(new ExecutionContext());
		//then
		IntStream.rangeClosed(1, EXPECTED_BATCH_TOTAL_DATA_SIZE).forEach((order) -> {
			try {
				assertThat(reader.read()).isNotNull(); // 더이상 읽을게 없어 null
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		assertThat(reader.read()).isNull();
		assertThat(reader.getPageSize()).isEqualTo(10);
	}

	@Autowired
	JdbcBatchItemWriter<OutBoxAlarmEvent> writer;

	@Test
	@DisplayName("데이터를 정상적으로 저장한다")
	void test() throws Exception {
		//given
		List<OutBoxAlarmEvent> chunkDataSet = LongStream.rangeClosed(1, EXPECTED_BATCH_TOTAL_DATA_SIZE)
			.mapToObj(alarmId -> OutBoxAlarmEvent.initialize(alarmId, "json data"))
			.toList();

		//when
		writer.write(new Chunk<>(chunkDataSet));

		//then
		List<OutBoxAlarmEvent> realOutBoxAlarmEvents = outBoxAlarmEventRepository.findAll();
		assertThat(realOutBoxAlarmEvents).hasSize(EXPECTED_BATCH_TOTAL_DATA_SIZE);
	}

	public StepExecution getStepExecution() {
		LocalTime startTime = TimeUtils.getNow().plusMinutes(40);
		LocalTime endTime = startTime.plusMinutes(1).minusSeconds(1);
		createAlarmContents(startTime, EXPECTED_BATCH_TOTAL_DATA_SIZE);

		return MetaDataInstanceFactory.createStepExecution(createAlarmJob(startTime, endTime));
	}

	private JobParameters createAlarmJob(LocalTime startTime, LocalTime endTime) {
		return new JobParametersBuilder()
			.addLong("today", (long)DayOfTheWeek.getToday())
			.addLocalTime("startTime", startTime)
			.addLocalTime("endTime", endTime)
			.toJobParameters();
	}

	private void createAlarmContents(LocalTime wakeupTime, int size) {
		LongStream.rangeClosed(1, 14).forEach((userId) -> {
			Tag economy = tagRepository.save(new Tag("경제"));
			Tag society = tagRepository.save(new Tag("사회"));

			Alarm alarm = alarmRepository.save(Alarm.initialize(userId));
			alarm.set(wakeupTime, DayOfTheWeek.getToday(), 1, 1, true);
			alarmRepository.save(alarm);

			alarmTagRepository.save(new AlarmTag(alarm, society));
			alarmTagRepository.save(new AlarmTag(alarm, economy));
		});
	}

}
