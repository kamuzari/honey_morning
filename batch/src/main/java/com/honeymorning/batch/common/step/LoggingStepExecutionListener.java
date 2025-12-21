package com.honeymorning.batch.common.step;

import java.time.Duration;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class LoggingStepExecutionListener implements StepExecutionListener {

	private static final Logger log = LoggerFactory.getLogger(LoggingStepExecutionListener.class);
	private static final long NOT_MEASURE_TIME = -9999L;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		log.info("[STEP 시작] {} - executionId={}, thread={}",
			stepExecution.getStepName(),
			stepExecution.getId(),
			Thread.currentThread().getName());
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		LocalDateTime start = stepExecution.getStartTime();
		LocalDateTime end = stepExecution.getEndTime();

		long durationMillis = calculateTime(start, end);

		log.info("[STEP 종료] {} - executionId={}, readCount={}, writeCount={}, commitCount={}, duration={}ms",
			stepExecution.getStepName(),
			stepExecution.getId(),
			stepExecution.getReadCount(),
			stepExecution.getWriteCount(),
			stepExecution.getCommitCount(),
			durationMillis);

		return stepExecution.getExitStatus();
	}

	private long calculateTime(LocalDateTime start, LocalDateTime end) {
		if (start != null && end != null) {
			return Duration.between(start, end).toMillis();
		}

		return NOT_MEASURE_TIME;
	}
}
