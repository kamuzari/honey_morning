package com.honeymorning.relay.config.messaging.kafka.constant;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka")
public record KafkaConsumerProperties(Map<String, ConsumerConfig> consumers) {

	public record ConsumerConfig(
		String topic,
		String groupId,
		String autoOffsetReset,
		boolean enableAutoCommit,
		String keyDeserializer,
		String valueDeserializer
	) {
		public String deadLetterTopic() {
			return TopicNameUtils.deadLetterTopic(topic);
		}
	}

	public ConsumerConfig cdc() {
		return consumers.get("cdc");
	}

	public ConsumerConfig failTts() {
		return consumers.get("fail-tts");
	}
}