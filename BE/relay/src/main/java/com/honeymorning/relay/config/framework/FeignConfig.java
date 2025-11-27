package com.honeymorning.relay.config.framework;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.honeymorning.common.exception.NotFoundResourceException;
import com.honeymorning.common.exception.UnExpectedFatalException;
import com.honeymorning.common.exception.constant.ErrorProtocol;

import feign.Request;
import feign.RetryableException;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableFeignClients(basePackages = "com.honeymorning.relay.*")
@Configuration
public class FeignConfig {

	private static final int RETRY_BASIC_PERIOD = 2000;
	private static final int MAX_RETRY_PERIOD = 3000;
	private static final int RETRY_MAX_ATTEMPTS = 3;
	private static final int REQUEST_CONNECT_TIMEOUT_MILLIS = 2000;
	private static final int REQUEST_READ_TIMEOUT_MILLIS = 2000;
	private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;

	@Bean
	public Retryer configureRetryPolicy() {
		return new Retryer.Default(RETRY_BASIC_PERIOD, MAX_RETRY_PERIOD, RETRY_MAX_ATTEMPTS) {
			@Override
			public void continueOrPropagate(RetryableException e) {
				log.info("Feign Retry Attempt execute .. : {}", e.getMessage(), e);
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
