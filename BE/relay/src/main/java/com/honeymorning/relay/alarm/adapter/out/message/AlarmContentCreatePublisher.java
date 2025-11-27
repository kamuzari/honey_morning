package com.honeymorning.relay.alarm.adapter.out.message;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.honeymorning.relay.alarm.adapter.in.cdc.CdcAlarmEventDto;
import com.honeymorning.relay.briefing.application.port.out.MessagePort;
import com.honeymorning.relay.config.messaging.rabbitmq.constant.ToAiQueue;

@Component
public class AlarmContentCreatePublisher implements MessagePort {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlarmContentCreatePublisher.class);

	private final RabbitTemplate rabbitTemplate;
	private final ToAiQueue toAiQueue;

	public AlarmContentCreatePublisher(RabbitTemplate rabbitTemplate, ToAiQueue toAiQueue) {
		this.rabbitTemplate = rabbitTemplate;
		this.toAiQueue = toAiQueue;
	}

	public void publish(String message, CdcAlarmEventDto scheduledAlarmContent) throws
		ExecutionException,
		InterruptedException,
		TimeoutException {

		var correlationData = new CorrelationData(UUID.randomUUID().toString());
		rabbitTemplate.convertAndSend(toAiQueue.getExchangeName(), toAiQueue.getRoutingKey(),
			scheduledAlarmContent.getPayload(), correlationData);
		CorrelationData.Confirm confirm = correlationData.getFuture().get(2, TimeUnit.SECONDS);
		if (!confirm.isAck()) {
			LOGGER.error("exception outbox event {}", message);
			throw new AmqpException("fail publisher confirm");
		}
	}
}
