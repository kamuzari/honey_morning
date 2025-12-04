package com.honeymorning.batch.alarm.application.step;

import static com.honeymorning.batch.alarm.application.constant.AlarmBatchConstant.READER;
import static com.honeymorning.batch.alarm.application.constant.AlarmBatchConstant.STEP;

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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeymorning.batch.alarm.adapter.out.persistence.entity.OutBoxAlarmEventEntity;
import com.honeymorning.batch.alarm.application.dto.ReadyAlarmDto;
import com.honeymorning.batch.alarm.application.step.processor.AlarmItemProcessor;
import com.honeymorning.batch.alarm.adapter.out.persistence.query.AlarmPagingQueryGenerator;
import com.honeymorning.batch.alarm.adapter.out.persistence.query.AlarmOutBoxEventWriteQueryGenerator;
import com.honeymorning.batch.common.step.LoggingStepExecutionListener;

@Configuration
public class AlarmContentStep {
	private static final AlarmPagingQueryGenerator READ_QUERY_GENERATOR = new AlarmPagingQueryGenerator();
	private static final AlarmOutBoxEventWriteQueryGenerator WRITE_QUERY_GENERATOR = new AlarmOutBoxEventWriteQueryGenerator();

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final DataSource primaryDataSource;
	private final DataSource mainReadOnlyDataSource;
	private final ObjectMapper objectMapper;
	private final int chunkSize;

	public AlarmContentStep(
		JobRepository jobRepository,
		PlatformTransactionManager transactionManager,
		DataSource primaryDataSource,
		@Qualifier("mainReadOnlyDataSource") DataSource mainReadOnlyDataSource,
		ObjectMapper objectMapper,
		@Value("${batch.alarm.chunk-size:1000}") int chunkSize
	) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.primaryDataSource = primaryDataSource;
		this.mainReadOnlyDataSource = mainReadOnlyDataSource;
		this.objectMapper = objectMapper;
		this.chunkSize = chunkSize;
	}

	@Bean
	public Step alarmStep(
		JdbcPagingItemReader<ReadyAlarmDto> reader,
		ItemProcessor<ReadyAlarmDto, OutBoxAlarmEventEntity> processor,
		ItemWriter<OutBoxAlarmEventEntity> writer) {

		return new StepBuilder(STEP, jobRepository)
			.<ReadyAlarmDto, OutBoxAlarmEventEntity>chunk(chunkSize, transactionManager)
			.reader(reader)
			.processor(processor)
			.writer(writer)
			.transactionManager(transactionManager)
			.listener(new LoggingStepExecutionListener())
			.build();
	}

	@Bean
	@StepScope
	public JdbcPagingItemReader<ReadyAlarmDto> reader(
		@Value("#{jobParameters['startAt']}") LocalDate startAt,
		@Value("#{jobParameters['startTime']}") LocalTime startTime,
		@Value("#{jobParameters['endTime']}") LocalTime endTime,
		@Value("#{jobParameters['today']}") Long today,
		@Value("#{stepExecutionContext['modular']}") Integer modular,
		@Value("#{stepExecutionContext['partition']}") Integer partition
	) {

		return new JdbcPagingItemReaderBuilder<ReadyAlarmDto>()
			.name(READER)
			.dataSource(mainReadOnlyDataSource)
			.queryProvider(READ_QUERY_GENERATOR.createQuery())
			.parameterValues(READ_QUERY_GENERATOR.getParameters(startTime, endTime, today, modular, partition))
			.rowMapper(READ_QUERY_GENERATOR.getRowMapper())
			.pageSize(chunkSize)
			.saveState(false)
			.build();
	}

	@Bean
	public ItemProcessor<ReadyAlarmDto, OutBoxAlarmEventEntity> processor() {
		return new AlarmItemProcessor(objectMapper);
	}

	@Bean
	public JdbcBatchItemWriter<OutBoxAlarmEventEntity> writer() {
		return new JdbcBatchItemWriterBuilder<OutBoxAlarmEventEntity>()
			.dataSource(primaryDataSource)
			.sql(WRITE_QUERY_GENERATOR.getSql())
			.itemSqlParameterSourceProvider(WRITE_QUERY_GENERATOR.getOutBoxAlarmEventItemSqlParameterSourceProvider())
			.build();
	}

}
