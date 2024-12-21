package com.sf.honeymorning.alarm.service;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.alarm.domain.entity.AlarmTag;
import com.sf.honeymorning.alarm.domain.repository.AlarmTagRepository;
import com.sf.honeymorning.alarm.service.dto.request.ToAIRequestDto;
import com.sf.honeymorning.alarm.service.dto.response.AiResponseDto;
import com.sf.honeymorning.config.RabbitConfig;
import com.sf.honeymorning.tag.entity.Tag;

@Transactional(readOnly = true)
@Component
public class AiClientService {
	public static final String PUBLISH_QUEUE_NAME = RabbitConfig.AI_GENERATIVE_ALARM_CONTENTS_QUEUE_NAME;
	public static final String SUBSCRIBE_QUEUE_NAME = RabbitConfig.AI_GENERATED_ALARM_CONTENTS_RESPONSE_QUEUE_NAME;

	private final RabbitTemplate rabbitTemplate;
	private final AlarmContentService alarmContentService;
	private final AlarmTagRepository alarmTagRepository;

	public AiClientService(RabbitTemplate rabbitTemplate, AlarmContentService alarmContentService,
		AlarmTagRepository alarmTagRepository) {
		this.rabbitTemplate = rabbitTemplate;
		this.alarmContentService = alarmContentService;
		this.alarmTagRepository = alarmTagRepository;

	}

	@Scheduled(fixedRate = 60000)
	public void ready() {
		alarmContentService.getReadyAlarm()
			.forEach(alarm -> {
				List<AlarmTag> alarmWithTag = alarmTagRepository.findByAlarmWithTag(alarm);
				List<String> tags = alarmWithTag.stream().map(AlarmTag::getTag)
					.map(Tag::getWord)
					.toList();

				publish(new ToAIRequestDto(alarm.getUserId(), tags));
			});
	}

	void publish(ToAIRequestDto aiDto) {
		rabbitTemplate.convertAndSend(PUBLISH_QUEUE_NAME, aiDto);
	}

	@RabbitListener(queues = SUBSCRIBE_QUEUE_NAME)
	public void createAlarmContents(AiResponseDto response) {
		alarmContentService.create(response);
	}
}
