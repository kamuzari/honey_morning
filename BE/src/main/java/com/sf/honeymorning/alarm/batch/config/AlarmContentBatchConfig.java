package com.sf.honeymorning.alarm.batch.config;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.honeymorning.alarm.batch.item.AlarmItemProcessor;
import com.sf.honeymorning.alarm.batch.item.dto.ReadyAlarmDto;
import com.sf.honeymorning.alarm.batch.outbox.OutBoxAlarmEvent;

@Configuration
public class AlarmContentBatchConfig {
	private static final int CHUNK_SIZE = 10;

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final DataSource dataSource;
	private final ObjectMapper objectMapper;

	public AlarmContentBatchConfig(JobRepository jobRepository,
		PlatformTransactionManager transactionManager,
		DataSource dataSource,
		ObjectMapper objectMapper) {

		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.dataSource = dataSource;
		this.objectMapper = objectMapper;
	}

	@Bean
	@StepScope
	public JdbcPagingItemReader<ReadyAlarmDto> reader(
		@Value("#{jobParameters['startTime']}") LocalTime startTime,
		@Value("#{jobParameters['endTime']}") LocalTime endTime,
		@Value("#{jobParameters['today']}") Long today) {

		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause("""
			SELECT user_id, GROUP_CONCAT(t.word ORDER BY t.word SEPARATOR ', ') AS tags
			""");
		queryProvider.setFromClause("""
			FROM alarms a
			JOIN alarm_tag at ON a.id = at.alarm_id
			JOIN tag t ON at.tag_id = t.tag_id
			""");
		queryProvider.setWhereClause("""
			WHERE is_active = true 
			AND (day_of_the_weeks & :dayOfWeekMask) != 0
			AND wake_up_time BETWEEN :startTime AND :endTime
			""");
		queryProvider.setGroupClause("GROUP BY user_id");
		queryProvider.setSortKeys(Map.of("user_id", Order.ASCENDING)); // 페이징

		return new JdbcPagingItemReaderBuilder<ReadyAlarmDto>()
			.name("alarmReader")
			.dataSource(dataSource)
			.queryProvider(queryProvider)
			.parameterValues(Map.of(
				"startTime", startTime,
				"endTime", endTime,
				"dayOfWeekMask", today
			))
			.rowMapper((rs, rowNum) -> new ReadyAlarmDto(
					rs.getLong("user_id"),
					Arrays.stream(rs.getString("tags")
						.split(",")).toList()
				)
			)
			.pageSize(10)
			.build();
	}

	@Bean
	public ItemProcessor<ReadyAlarmDto, OutBoxAlarmEvent> processor() {
		return new AlarmItemProcessor(objectMapper);
	}

	@Bean
	public JdbcBatchItemWriter<OutBoxAlarmEvent> writer() {
		return new JdbcBatchItemWriterBuilder<OutBoxAlarmEvent>()
			.dataSource(dataSource)
			.sql("""
				    INSERT INTO outbox_alarm_event (alarm_id, event_status, event_type, payload, create_at)
				    VALUES (:alarmId, :eventStatus, :eventType, :payload, :createAt)
				""")
			.itemSqlParameterSourceProvider(item -> {
				MapSqlParameterSource params = new MapSqlParameterSource();
				params.addValue("alarmId", item.getAlarmId());
				params.addValue("eventStatus", item.getEventStatus().name());
				params.addValue("eventType", item.getEventType());
				params.addValue("payload", item.getPayload());
				params.addValue("createAt", item.getCreateAt());
				return params;
			})
			.build();
	}

	@Bean
	public Step alarmStep(JdbcPagingItemReader<ReadyAlarmDto> reader,
		ItemProcessor<ReadyAlarmDto, OutBoxAlarmEvent> processor,
		ItemWriter<OutBoxAlarmEvent> writer) {
		return new StepBuilder("alarmStep", jobRepository)
			.<ReadyAlarmDto, OutBoxAlarmEvent>chunk(CHUNK_SIZE, transactionManager)
			.reader(reader)
			.processor(processor)
			.writer(writer)
			.transactionManager(transactionManager)
			.build();
	}

	@Bean
	public Job alarmJob(Step alarmStep) {
		return new JobBuilder("alarmJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(alarmStep)
			.build();
	}
}
