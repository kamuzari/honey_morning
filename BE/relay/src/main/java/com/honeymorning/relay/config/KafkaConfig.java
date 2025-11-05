package com.honeymorning.relay.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

import com.honeymorning.relay.config.constant.KafkaTopicProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableConfigurationProperties({KafkaTopicProperties.class})
public class KafkaConfig {
	private final KafkaTopicProperties kafkaTopicProperties;
	private final long INITIAL_INTERVAL = 1000L;
	private final double MULTIPLIER = 2.0;
	private final long MAX_RETRY_TIME = 3000L;

	public KafkaConfig(KafkaTopicProperties kafkaTopicProperties) {
		this.kafkaTopicProperties = kafkaTopicProperties;
	}

	@Bean
	public NewTopic toAiTopic() {
		return TopicBuilder.name(kafkaTopicProperties.topic())
			.partitions(3)
			.replicas(3)
			.build();
	}

	@Bean
	public NewTopic toAiDeadLetterTopic() {
		return TopicBuilder.name(kafkaTopicProperties.deadLetterTopic())
			.partitions(3)
			.replicas(3)
			.build();
	}

	@Bean
	public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> template) {
		var recoverer = new DeadLetterPublishingRecoverer(template);
		ExponentialBackOff backOff = new ExponentialBackOff(INITIAL_INTERVAL, MULTIPLIER);
		backOff.setMaxElapsedTime(MAX_RETRY_TIME);

		return new DefaultErrorHandler(recoverer, backOff);
	}

}
