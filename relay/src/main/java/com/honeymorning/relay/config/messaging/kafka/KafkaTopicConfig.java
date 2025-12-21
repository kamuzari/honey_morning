package com.honeymorning.relay.config.messaging.kafka;

import java.util.stream.Stream;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import com.honeymorning.relay.config.messaging.kafka.constant.KafkaTopicProperties;
import com.honeymorning.relay.config.messaging.kafka.constant.KafkaTopicProperties.TopicConfig;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({KafkaTopicProperties.class})
public class KafkaTopicConfig {

	private final KafkaTopicProperties topicProperties;

	@Bean
	public KafkaAdmin.NewTopics topics() {
		return new KafkaAdmin.NewTopics(
			topicProperties.topics().values().stream()
				.flatMap(config -> Stream.of(
					buildTopic(config),
					buildDeadLetterTopic(config)
				))
				.toArray(NewTopic[]::new)
		);
	}

	private NewTopic buildTopic(TopicConfig config) {
		return TopicBuilder.name(config.name())
			.partitions(config.partitions())
			.replicas(config.replicas())
			.build();
	}

	private NewTopic buildDeadLetterTopic(TopicConfig config) {
		return TopicBuilder.name(config.deadLetterName())
			.partitions(config.partitions())
			.replicas(config.replicas())
			.build();
	}
}