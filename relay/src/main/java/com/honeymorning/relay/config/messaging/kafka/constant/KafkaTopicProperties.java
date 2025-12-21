package com.honeymorning.relay.config.messaging.kafka.constant;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka")
public record KafkaTopicProperties(Map<String, TopicConfig> topics) {

	public record TopicConfig(
		String name,
		int partitions,
		int replicas
	) {
		public String deadLetterName() {
			return TopicNameUtils.deadLetterTopic(name);
		}
	}
}