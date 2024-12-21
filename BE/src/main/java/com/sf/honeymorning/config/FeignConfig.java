package com.sf.honeymorning.config;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sf.honeymorning.common.exception.model.ErrorProtocol;
import com.sf.honeymorning.common.exception.model.NotFoundResourceException;
import com.sf.honeymorning.common.exception.model.UnExpectedFatalException;

import feign.Request;
import feign.RetryableException;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableFeignClients(basePackages = "com.sf.honeymorning.alarm.client")
@Configuration
public class FeignConfig {

	private static final int RETRY_BASIC_PERIOD = 1000;
	private static final int MAX_RETRY_PERIOD = 3000;
	private static final int RETRY_MAX_ATTEMPTS = 3;

	private static final int REQUEST_CONNECT_TIMEOUT_MILLIS = 5000;
	private static final int REQUEST_READ_TIMEOUT_MILLIS = 10000;
	private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;

	@Bean
	public Retryer configureRetryPolicy() {
		return new Retryer.Default(RETRY_BASIC_PERIOD, MAX_RETRY_PERIOD, RETRY_MAX_ATTEMPTS) {
			@Override
			public void continueOrPropagate(RetryableException e) {
				log.info("Feign Retry Attempt execute .. : {}", e.getMessage());
				super.continueOrPropagate(e);
			}
		};
	}

	@Bean
	public Request.Options configureTimeOutOptionPolicy() {
		return new Request.Options(
			REQUEST_CONNECT_TIMEOUT_MILLIS,
			DEFAULT_TIME_UNIT,
			REQUEST_READ_TIMEOUT_MILLIS,
			DEFAULT_TIME_UNIT,
			true);
	}

	@Bean
	public ErrorDecoder errorDecoder() {
		return (methodKey, response) -> {
			if (response.status() == 404) {
				return new NotFoundResourceException(
					MessageFormat.format("request resource not found: methodkey - {0}, response - {1}", methodKey,
						response),
					ErrorProtocol.BUSINESS_VIOLATION
				);
			}

			return new UnExpectedFatalException(
				MessageFormat.format("request fatal error: methodkey - {0}, response - {1}", methodKey, response),
				ErrorProtocol.BUSINESS_VIOLATION
			);
		};
	}
}
