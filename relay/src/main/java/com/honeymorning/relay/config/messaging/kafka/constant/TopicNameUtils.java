package com.honeymorning.relay.config.messaging.kafka.constant;

import static java.lang.String.join;

public class TopicNameUtils {
	private static final String DEAD_LETTER_TOPIC_SUFFIX = "dlt";

	public static String deadLetterTopic(String basicTopicName) {
		return join("-", basicTopicName, DEAD_LETTER_TOPIC_SUFFIX);
	}
}
