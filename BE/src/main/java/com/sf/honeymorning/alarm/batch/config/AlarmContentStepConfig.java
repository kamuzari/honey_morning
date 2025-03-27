package com.sf.honeymorning.alarm.batch.config;

import static com.sf.honeymorning.alarm.batch.config.AlarmContentJobConfig.ALARM_TO_ALARM_EVENT_CREATE_JOB;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.honeymorning.alarm.batch.item.dto.ReadyAlarmDto;
import com.sf.honeymorning.alarm.batch.item.processor.AlarmItemProcessor;
import com.sf.honeymorning.alarm.batch.item.reader.AlarmPagingQueryGenerator;
import com.sf.honeymorning.alarm.batch.item.writer.AlarmOutBoxEventWriteQueryGenerator;
import com.sf.honeymorning.alarm.batch.outbox.OutBoxAlarmEvent;
import com.sf.honeymorning.alarm.batch.step.LoggingStepExecutionListener;

@Configuration
public class AlarmContentStepConfig {
	private static final AlarmPagingQueryGenerator READ_QUERY_GENERATOR = new AlarmPagingQueryGenerator();
	private static final AlarmOutBoxEventWriteQueryGenerator WRITE_QUERY_GENERATOR = new AlarmOutBoxEventWriteQueryGenerator();

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final DataSource dataSource;
	private final ObjectMapper objectMapper;
	private final int chunkSize;

	public AlarmContentStepConfig(
		JobRepository jobRepository,
		PlatformTransactionManager transactionManager,
		DataSource dataSource,
		ObjectMapper objectMapper,
		@Value("${batch.alarm.chunk-size:1000}") int chunkSize
	) {

		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.dataSource = dataSource;
		this.objectMapper = objectMapper;
		this.chunkSize = chunkSize;
	}

	@Bean
	@StepScope
	public JdbcPagingItemReader<ReadyAlarmDto> reader(
		@Value("#{jobParameters['startAt']}") LocalDate startAt,
		@Value("#{jobParameters['startTime']}") LocalTime startTime,
		@Value("#{jobParameters['endTime']}") LocalTime endTime,
		@Value("#{jobParameters['today']}") Long today,
		@Value("#{stepExecutionContext['modular']}") Integer modular,
		@Value("#{stepExecutionContext['partitionSize']}") Integer partitionSize
	) {

		return new JdbcPagingItemReaderBuilder<ReadyAlarmDto>()
			.name(ALARM_TO_ALARM_EVENT_CREATE_JOB + "_reader")
			.dataSource(dataSource)
			.queryProvider(READ_QUERY_GENERATOR.createQuery())
			.parameterValues(READ_QUERY_GENERATOR.getParameters(startTime, endTime, today, modular, partitionSize))
			.rowMapper(READ_QUERY_GENERATOR.getRowMapper())
			.pageSize(chunkSize)
			.saveState(false)
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
			.sql(WRITE_QUERY_GENERATOR.getSql())
			.itemSqlParameterSourceProvider(WRITE_QUERY_GENERATOR.getOutBoxAlarmEventItemSqlParameterSourceProvider())
			.build();
	}

	@Bean
	public Step alarmStep(
		JdbcPagingItemReader<ReadyAlarmDto> reader,
		ItemProcessor<ReadyAlarmDto, OutBoxAlarmEvent> processor,
		ItemWriter<OutBoxAlarmEvent> writer) {

		return new StepBuilder(ALARM_TO_ALARM_EVENT_CREATE_JOB + "_step", jobRepository)
			.<ReadyAlarmDto, OutBoxAlarmEvent>chunk(chunkSize, transactionManager)
			.reader(reader)
			.processor(processor)
			.writer(writer)
			.transactionManager(transactionManager)
			.listener(new LoggingStepExecutionListener())
			.build();
	}

}
