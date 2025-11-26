package com.honeymorning.relay.config.constant.kafka;

import static java.lang.String.join;

public class TopicNameUtils {
	private static final String DEAD_LETTER_TOPIC_SUFFIX = "DLT";

	public static String deadLetterTopic(String basicTopicName) {
		return join(".", basicTopicName, DEAD_LETTER_TOPIC_SUFFIX);
	}
}
