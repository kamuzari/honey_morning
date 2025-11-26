package com.honeymorning.relay.config.constant.kafka;

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

	public TopicConfig cdc() {
		return topics.get("cdc");
	}

	public TopicConfig failTts() {
		return topics.get("fail-tts");
	}

	public TopicConfig fromAi() {
		return topics.get("fromAi");
	}
}