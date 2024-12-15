package com.sf.honeymorning.alarm.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.alarm.domain.entity.AlarmTag;
import com.sf.honeymorning.alarm.domain.repository.AlarmTagRepository;
import com.sf.honeymorning.alarm.service.dto.request.ToAIRequestDto;
import com.sf.honeymorning.alarm.service.dto.response.AiQuizDto;
import com.sf.honeymorning.alarm.service.dto.response.AiResponseDto;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.entity.BriefingTag;
import com.sf.honeymorning.brief.entity.TopicModelWord;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.common.exception.model.ErrorProtocol;
import com.sf.honeymorning.config.RabbitConfig;
import com.sf.honeymorning.quiz.entity.Quiz;
import com.sf.honeymorning.tag.entity.Tag;
import com.sf.honeymorning.util.TtsUtil;

@Transactional(readOnly = true)
@Component
public class AiClientService {
	public static final String PUBLISH_QUEUE_NAME = RabbitConfig.AI_GENERATIVE_ALARM_CONTENTS_QUEUE_NAME;
	public static final String SUBSCRIBE_QUEUE_NAME = RabbitConfig.AI_GENERATED_ALARM_CONTENTS_RESPONSE_QUEUE_NAME;

	private final PreparedAlarmContentService preparedAlarmContentService;
	private final AlarmTagRepository alarmTagRepository;
	private final RabbitTemplate rabbitTemplate;
	private final TtsUtil ttsUtil;

	public AiClientService(PreparedAlarmContentService preparedAlarmContentService,
		AlarmTagRepository alarmTagRepository,
		RabbitTemplate rabbitTemplate, TtsUtil ttsUtil) {
		this.preparedAlarmContentService = preparedAlarmContentService;
		this.alarmTagRepository = alarmTagRepository;
		this.rabbitTemplate = rabbitTemplate;
		this.ttsUtil = ttsUtil;
	}

	@Scheduled(fixedRate = 60000)
	public void ready() {
		preparedAlarmContentService.getReadyAlarm()
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
	public void setAllBriefing(AiResponseDto response) {
		String wakeUpCallPath = response.AiWakeUpCallPath();

		String briefingVoice = textToSpeechSafe(response.aiBriefings().voiceContent(), "summaryText");
		Map<AiQuizDto, String> quizVoices = response.aiQuizzes()
			.stream()
			.map(aiQuizDto -> Map.entry(aiQuizDto, textToSpeechSafe(aiQuizDto.problem(), "quiz")))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		preparedAlarmContentService.bulkSave(
			new Briefing(
				response.userId(),
				response.aiBriefings().voiceContent(),
				response.aiBriefings().readContent(),
				briefingVoice,
				response.requestTags().stream().map(requestTag -> new BriefingTag(new Tag(requestTag))).toList(),
				response.aiQuizzes().stream().map(aiQuizDto ->
					new Quiz(aiQuizDto.problem(),
						aiQuizDto.answer(),
						aiQuizDto.selections(),
						quizVoices.get(aiQuizDto)
					)).toList(),
				response.aiTopics().stream().map(aiTopicDto -> new TopicModelWord(
					aiTopicDto.sectionId(),
					aiTopicDto.word(),
					aiTopicDto.weight())).toList()
			));
	}

	private String textToSpeechSafe(String text, String type) {
		try {
			return ttsUtil.textToSpeech(text, type);
		} catch (IOException e) {
			throw new BusinessException("TTS 생성 실패", e, ErrorProtocol.READY_TO_ALARM_VOICE_TRANSLATION_FAIL);
		}
	}

}
