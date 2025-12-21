package com.honeymorning.common.event.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

public class EventDispatcherHandler {
	private static final Logger logger = LoggerFactory.getLogger(EventDispatcherHandler.class);
	private static ApplicationEventPublisher publisher;

	public static void setPublisher(ApplicationEventPublisher publisher) {
		EventDispatcherHandler.publisher = publisher;
	}

	public static void raise(Object event) {
		logger.info("raise event: {}", event);

		if (publisher != null) {
			publisher.publishEvent(event);
		}
	}
}
