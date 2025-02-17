package com.sf.honeymorning.alarm.cdc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.honeymorning.config.RabbitConfig;

@Component
public class AlarmEventCdcConsumer {
	private static final Logger log = LoggerFactory.getLogger(AlarmEventCdcConsumer.class);
	private static final String PUBLISH_QUEUE_NAME = RabbitConfig.AI_GENERATIVE_ALARM_CONTENTS_QUEUE_NAME;

	private final ObjectMapper objectMapper;
	private final RabbitTemplate rabbitTemplate;

	public AlarmEventCdcConsumer(ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
		this.objectMapper = objectMapper;
		this.rabbitTemplate = rabbitTemplate;
	}

	@KafkaListener(topics = "${spring.kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
	public void consumeOutboxEvent(String message, Acknowledgment acknowledgment) {
		log.info("consume outbox event {}", message);
		try {
			CdcAlarmEventDto scheduledAlarmContent = objectMapper.convertValue(
				objectMapper.readTree(message).path("payload"),
				CdcAlarmEventDto.class);
			rabbitTemplate.convertAndSend(PUBLISH_QUEUE_NAME, scheduledAlarmContent.getPayload());
			acknowledgment.acknowledge();
		} catch (Exception e) {
			log.error("Error parsing or sending message to RabbitMQ", e);
		}
	}
}
