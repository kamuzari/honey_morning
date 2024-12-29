package com.sf.honeymorning.alarm.batch.config;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.sf.honeymorning.alarm.batch.item.AlarmItemProcessor;
import com.sf.honeymorning.alarm.batch.item.AlarmItemWriter;
import com.sf.honeymorning.alarm.batch.item.dto.ReadyAlarmDto;
import com.sf.honeymorning.alarm.service.dto.request.ToAIRequestDto;

@Configuration
public class AlarmContentBatchConfig {
	private static final int CHUNK_SIZE = 10;

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final DataSource dataSource;
	private final RabbitTemplate rabbitTemplate;

	public AlarmContentBatchConfig(JobRepository jobRepository,
		PlatformTransactionManager transactionManager,
		DataSource dataSource,
		RabbitTemplate rabbitTemplate) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.dataSource = dataSource;
		this.rabbitTemplate = rabbitTemplate;
	}

	@Bean
	@StepScope
	public JdbcPagingItemReader<ReadyAlarmDto> alarmReader(
		@Value("#{jobParameters['startTime']}") LocalTime startTime,
		@Value("#{jobParameters['endTime']}") LocalTime endTime,
		@Value("#{jobParameters['today']}") Long today) {
		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause("""
			SELECT a.user_id, GROUP_CONCAT(t.word ORDER BY t.word SEPARATOR ', ') AS tags
			""");
		queryProvider.setFromClause("""
			FROM alarms a
			JOIN alarm_tag at ON a.id = at.alarm_id
			JOIN tag t ON at.tag_id = t.tag_id
			""");
		queryProvider.setWhereClause("""
			WHERE a.is_active = true 
			AND (a.day_of_the_weeks & :dayOfWeekMask) != 0
			AND a.wake_up_time BETWEEN :startTime AND :endTime
			""");
		queryProvider.setGroupClause("GROUP BY a.user_id");
		queryProvider.setSortKeys(Map.of("a.user_id", Order.ASCENDING)); // 페이징

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
	public ItemProcessor<ReadyAlarmDto, ToAIRequestDto> alarmProcessor() {
		return new AlarmItemProcessor();
	}

	@Bean
	public ItemWriter<ToAIRequestDto> alarmWriter() {
		return new AlarmItemWriter(rabbitTemplate);
	}

	@Bean
	public Step alarmStep(JdbcPagingItemReader<ReadyAlarmDto> reader,
		ItemProcessor<ReadyAlarmDto, ToAIRequestDto> processor,
		ItemWriter<ToAIRequestDto> writer) {
		return new StepBuilder("alarmStep", jobRepository)
			.<ReadyAlarmDto, ToAIRequestDto>chunk(CHUNK_SIZE, transactionManager)
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
