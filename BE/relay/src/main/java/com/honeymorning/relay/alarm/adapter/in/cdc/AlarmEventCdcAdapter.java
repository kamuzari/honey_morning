package com.honeymorning.relay.alarm.adapter.in.cdc;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.honeymorning.relay.briefing.application.port.out.MessagePort;

@Component
public class AlarmEventCdcAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlarmEventCdcAdapter.class);

	private final MessagePort messagePort;

	public AlarmEventCdcAdapter(MessagePort messagePort) {
		this.messagePort = messagePort;
	}

	@KafkaListener(
		topics = "${app.kafka.topics.to-ai-cdc.name}",
		groupId = "${app.kafka.consumers.to-ai-cdc.group-id}",
		containerFactory = "cdcKafkaListenerContainerFactory"
	)
	public void consumeOutboxEvent(CdcAlarmEventDto dto, Acknowledgment acknowledgment)
		throws ExecutionException, InterruptedException, TimeoutException {

		LOGGER.info("consume outbox event {}", dto);
		messagePort.publish(dto);
		acknowledgment.acknowledge();
	}

	@KafkaListener(
		topics = "${app.kafka.topics.to-ai-cdc.name}.DLT",
		groupId = "${app.kafka.consumers.to-ai-cdc.group-id}",
		containerFactory = "cdcKafkaListenerContainerFactory"
	)
	public void consumeOutboxEventDlt(CdcAlarmEventDto dto, Acknowledgment acknowledgment)
		throws ExecutionException, InterruptedException, TimeoutException {

		LOGGER.warn("consume outbox DLT event {}", dto);
		messagePort.publish(dto);
		acknowledgment.acknowledge();
	}
}
