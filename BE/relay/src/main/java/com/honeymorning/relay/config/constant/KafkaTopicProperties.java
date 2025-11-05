package com.honeymorning.relay.config.constant;

import static java.lang.String.join;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka.consumer")
public record KafkaTopicProperties(String topic) {
	private static final String DEAD_LETTER_TOPIC_SUFFIX = ".DLT";

	public String deadLetterTopic() {
		return join(this.topic(), DEAD_LETTER_TOPIC_SUFFIX);
	}
}
