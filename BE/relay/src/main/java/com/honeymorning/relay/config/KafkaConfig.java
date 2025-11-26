package com.honeymorning.relay.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

import com.honeymorning.relay.config.constant.kafka.KafkaTopicProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({KafkaTopicProperties.class})
public class KafkaConfig {

	private final KafkaProperties kafkaProperties;
	private final KafkaTopicProperties topicProperties;

	private static final long INITIAL_INTERVAL = 1000L;
	private static final double MULTIPLIER = 2.0;
	private static final long MAX_RETRY_TIME = 3000L;

	@Bean
	public NewTopic cdcTopic() {
		var config = topicProperties.cdc();
		return TopicBuilder.name(config.name())
			.partitions(config.partitions())
			.replicas(config.replicas())
			.build();
	}

	@Bean
	public NewTopic cdcDeadLetterTopic() {
		var config = topicProperties.cdc();

		return TopicBuilder.name(config.deadLetterName())
			.partitions(config.partitions())
			.replicas(config.replicas())
			.build();
	}

	@Bean
	public NewTopic failTtsTopic() {
		var config = topicProperties.failTts();

		return TopicBuilder.name(config.name())
			.partitions(config.partitions())
			.replicas(config.replicas())
			.build();
	}

	@Bean
	public NewTopic failTtsDeadLetterTopic() {
		var config = topicProperties.failTts();

		return TopicBuilder.name(config.deadLetterName())
			.partitions(config.partitions())
			.replicas(config.replicas())
			.build();
	}

	@Bean
	public NewTopic fromAiTopic() {
		var config = topicProperties.fromAi();

		return TopicBuilder.name(config.name())
			.partitions(config.partitions())
			.replicas(config.replicas())
			.build();
	}

	@Bean
	public NewTopic fromAiDeadLetterTopic() {
		var config = topicProperties.fromAi();

		return TopicBuilder.name(config.deadLetterName())
			.partitions(config.partitions())
			.replicas(config.replicas())
			.build();
	}

	@Bean
	public ConsumerFactory<String, String> consumerFactory() {
		Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
		return new DefaultKafkaConsumerFactory<>(props);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
		ConsumerFactory<String, String> consumerFactory,
		DefaultErrorHandler errorHandler
	) {
		var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
		factory.setConsumerFactory(consumerFactory);
		factory.getContainerProperties().setAckMode(AckMode.RECORD);
		factory.setCommonErrorHandler(errorHandler);

		return factory;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> cdcKafkaListenerContainerFactory(
		ConsumerFactory<String, String> consumerFactory,
		DefaultErrorHandler errorHandler
	) {
		var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
		factory.setConsumerFactory(consumerFactory);
		factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
		factory.setCommonErrorHandler(errorHandler);
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
