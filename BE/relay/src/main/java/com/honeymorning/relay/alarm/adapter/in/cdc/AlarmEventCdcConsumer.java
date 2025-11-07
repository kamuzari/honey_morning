package com.honeymorning.relay.alarm.adapter.in.cdc;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeymorning.relay.briefing.application.port.out.MessagePort;

@Component
public class AlarmEventCdcConsumer {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlarmEventCdcConsumer.class);

	private final ObjectMapper objectMapper;
	private final MessagePort messagePort;

	public AlarmEventCdcConsumer(ObjectMapper objectMapper, MessagePort messagePort) {
		this.objectMapper = objectMapper;
		this.messagePort = messagePort;
	}

	@KafkaListener(
		topics = "${spring.kafka.consumer.topic}",
		groupId = "${spring.kafka.consumer.group-id}")
	public void consumeOutboxEvent(
		String message,
		Acknowledgment acknowledgment
	) throws JacksonException, ExecutionException, InterruptedException, TimeoutException {

		LOGGER.info("consume outbox event {}", message);
		var scheduledAlarmContent = objectMapper.convertValue(
			objectMapper.readTree(message).path("payload"),
			CdcAlarmEventDto.class);
		messagePort.publish(message, scheduledAlarmContent);
		acknowledgment.acknowledge();
	}
}
