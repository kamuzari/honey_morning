package com.honeymorning.relay.briefing.adapter.in.consumer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.honeymorning.relay.briefing.application.port.in.AlarmContentCommandUseCase;
import com.honeymorning.relay.briefing.application.service.dto.AiResponseDto;
import com.honeymorning.relay.config.constant.rabbitmq.FromAiQueue;
import com.rabbitmq.client.Channel;

@Transactional(readOnly = true)
@Component
public class AiClientConsumer {
	private static final Logger log = LoggerFactory.getLogger(AiClientConsumer.class);

	private final AlarmContentCommandUseCase alarmContentCommandUseCase;

	public AiClientConsumer(AlarmContentCommandUseCase alarmContentCommandUseCase) {
		this.alarmContentCommandUseCase = alarmContentCommandUseCase;
	}

	@RabbitListener(queues = FromAiQueue.NAME, ackMode = "MANUAL")
	public void createAlarmContents(
		AiResponseDto response,
		Channel channel,
		@Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {

		try {
			alarmContentCommandUseCase.create(response);
			channel.basicAck(tag, false);
		} catch (Exception e) {
			log.error("fallback fail : {}, {}", e.getMessage(), e);
			channel.basicNack(tag, false, false);
		}
	}
}
