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

	public TopicConfig cdc() {
		return topics.get("cdc");
	}

	public TopicConfig failTts() {
		return topics.get("fail-tts");
	}

	public TopicConfig fromAi() {
		return topics.get("fromAi");
	}

	public TopicConfig fromAiStore() {
		return topics.get("from-ai-store");
	}

	public TopicConfig fromAiBriefingTts() {
		return topics.get("from-ai-briefing-tts");
	}

	public TopicConfig fromAiQuiz1Tts() {
		return topics.get("from-ai-quiz1-tts");
	}

	public TopicConfig fromAiQuiz2Tts() {
		return topics.get("from-ai-quiz2-tts");
	}
}