package com.sf.honeymorning.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sf.honeymorning.common.event.service.EventsProducer;

@Configuration
public class ApplicationEventConfig {
	private final ApplicationContext applicationContext;

	public ApplicationEventConfig(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Bean
	public InitializingBean initializeEvents() {
		return () -> EventsProducer.setPublisher(applicationContext);
	}
}
