package com.sf.honeymorning.common.config;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@TestConfiguration
public class AsyncTestConfig implements AsyncConfigurer {
	public static AtomicReference<Throwable> capturedError = new AtomicReference<>();
	public static CountDownLatch errorLatch = new CountDownLatch(1);

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return (throwable, method, obj) -> {
			capturedError.set(throwable);
			errorLatch.countDown();
		};
	}
}
