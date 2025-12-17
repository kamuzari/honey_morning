package com.honeymorning.relay.config.messaging.kafka;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.util.backoff.ExponentialBackOff;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

	private static final long INITIAL_INTERVAL = 1000L;
	private static final double MULTIPLIER = 2.0;
	private static final long MAX_RETRY_TIME = 3000L;
	private final KafkaProperties kafkaProperties;

	@Bean
	public ConsumerFactory<String, String> consumerFactory() {
		Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
		return new DefaultKafkaConsumerFactory<>(props);
	}

	@Bean
	public RecordMessageConverter messageConverter() {
		return new JsonMessageConverter();
	}

	@Primary
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
		ConsumerFactory<String, String> consumerFactory,
		DefaultErrorHandler errorHandler,
		RecordMessageConverter messageConverter
	) {
		var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
		factory.setConsumerFactory(consumerFactory);
		factory.getContainerProperties().setAckMode(AckMode.RECORD);
		factory.setCommonErrorHandler(errorHandler);
		factory.setRecordMessageConverter(messageConverter);

		return factory;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> cdcKafkaListenerContainerFactory(
		ConsumerFactory<String, String> consumerFactory,
		DefaultErrorHandler errorHandler,
		RecordMessageConverter messageConverter
	) {
		var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
		factory.setConsumerFactory(consumerFactory);
		factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
		factory.setCommonErrorHandler(errorHandler);
		factory.setRecordMessageConverter(messageConverter);
		return factory;
	}

	@Bean
	public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> template) {
		var recoverer = new DeadLetterPublishingRecoverer(template);
		ExponentialBackOff backOff = new ExponentialBackOff(INITIAL_INTERVAL, MULTIPLIER);
		backOff.setMaxElapsedTime(MAX_RETRY_TIME);

		return new DefaultErrorHandler(recoverer, backOff);
	}
}