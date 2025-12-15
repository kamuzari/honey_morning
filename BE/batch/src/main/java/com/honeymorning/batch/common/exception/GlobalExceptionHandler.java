package com.honeymorning.batch.common.exception;

import static org.springframework.http.ResponseEntity.status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
	@ExceptionHandler(JobExecutionException.class)
	public ResponseEntity<ErrorResponse> handleRestartFailed(JobExecutionException e) {
		ErrorProtocol batchJobRunError = ErrorProtocol.BATCH_JOB_RUN_ERROR;
		ErrorResponse body = ErrorResponse.of(batchJobRunError, "");

		log.error("Job 에러 발생: {} -  {}", batchJobRunError.getInternalMessage(), e.getMessage());

		return status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(body);
	}
}

