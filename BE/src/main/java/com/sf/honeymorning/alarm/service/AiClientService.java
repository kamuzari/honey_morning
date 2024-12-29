package com.sf.honeymorning.alarm.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.alarm.service.dto.response.AiResponseDto;
import com.sf.honeymorning.config.RabbitConfig;

@Transactional(readOnly = true)
@Component
public class AiClientService {
	public static final String SUBSCRIBE_QUEUE_NAME = RabbitConfig.AI_GENERATED_ALARM_CONTENTS_RESPONSE_QUEUE_NAME;

	private final AlarmContentService alarmContentService;

	public AiClientService(AlarmContentService alarmContentService) {
		this.alarmContentService = alarmContentService;
	}

	@RabbitListener(queues = SUBSCRIBE_QUEUE_NAME)
	public void createAlarmContents(AiResponseDto response) {
		alarmContentService.create(response);
	}
}
