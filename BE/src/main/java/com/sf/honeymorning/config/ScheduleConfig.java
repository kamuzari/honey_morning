package com.sf.honeymorning.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@EnableScheduling
@EnableAsync
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
		threadPoolTaskScheduler.setThreadGroupName("scheduler thread pool");
		threadPoolTaskScheduler.setThreadNamePrefix("scheduler-thread-");
		threadPoolTaskScheduler.initialize();

		taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
	}
}
