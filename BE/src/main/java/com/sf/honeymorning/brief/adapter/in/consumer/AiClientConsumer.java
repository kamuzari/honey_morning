package com.sf.honeymorning.brief.adapter.in.consumer;

import java.io.IOException;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.rabbitmq.client.Channel;
import com.sf.honeymorning.brief.application.port.in.AlarmContentCommandUseCase;
import com.sf.honeymorning.alarm.application.service.dto.response.AiResponseDto;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.common.exception.model.constant.ErrorProtocol;
import com.sf.honeymorning.config.RabbitConfig;

@Transactional(readOnly = true)
@Component
public class AiClientConsumer {
	private static final Logger log = LoggerFactory.getLogger(AiClientConsumer.class);
	public static final String SUBSCRIBE_QUEUE_NAME = RabbitConfig.AI_GENERATED_ALARM_CONTENTS_RESPONSE_QUEUE_NAME;

	private final AlarmContentCommandUseCase alarmContentCommandUseCase;

	public AiClientConsumer(AlarmContentCommandUseCase alarmContentCommandUseCase) {
		this.alarmContentCommandUseCase = alarmContentCommandUseCase;
	}

	@RabbitListener(queues = SUBSCRIBE_QUEUE_NAME, ackMode = "MANUAL")
	public void createAlarmContents(
		AiResponseDto response,
		Channel channel,
		@Header(AmqpHeaders.DELIVERY_TAG) long tag) {

		try {
			alarmContentCommandUseCase.create(response);
			channel.basicAck(tag, false);
		} catch (Exception e) {
			log.error(e.getMessage(), e);

			try {
				channel.basicNack(tag, false, false);
			} catch (IOException ex) {
				throw new AmqpException("NACK 전송 실패", ex);
			}

			throw new BusinessException(
				MessageFormat.format("ai 로부터 받은 콘텐츠 사후처리에 실패했습니다. response : {0}", response),
				ErrorProtocol.BUSINESS_VIOLATION
			);
		}
	}
}
