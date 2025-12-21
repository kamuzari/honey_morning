package com.honeymorning.relay.briefing.adapter.out.persistence.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.honeymorning.common.domain.briefing.entity.BriefingEntity;
import com.honeymorning.common.domain.briefing.entity.BriefingTagEntity;
import com.honeymorning.common.domain.briefing.entity.QuizEntity;
import com.honeymorning.common.domain.briefing.entity.TopicModelWordEntity;
import com.honeymorning.relay.briefing.application.domain.EmptyBriefingTts;
import com.honeymorning.relay.briefing.application.domain.LatestBriefing;
import com.honeymorning.relay.briefing.application.domain.TextToSpeechContent;
import com.honeymorning.relay.briefing.application.service.dto.AiResponseDto;

@Component
public class BriefingPersistenceMapper {
	public BriefingEntity toTotalAlarmContent(AiResponseDto response) {
		return new BriefingEntity(
			response.userId(),
			response.aiBriefings().summaryContent(),
			response.aiBriefings().readContent(),
			response.AiWakeUpCallPath(),
			response.requestTags().stream().map(BriefingTagEntity::new).collect(Collectors.toSet()),
			response.aiQuizzes().stream().map(aiQuizDto ->
				new QuizEntity(aiQuizDto.problem(),
					aiQuizDto.answer(),
					aiQuizDto.selections(),
					aiQuizDto.order()
				)).collect(Collectors.toSet()),
			response.aiTopics().stream().map(aiTopicDto -> new TopicModelWordEntity(
					aiTopicDto.sectionId(),
					aiTopicDto.word(),
					aiTopicDto.weight()))
				.collect(Collectors.toSet())
		);
	}

	public TextToSpeechContent toTextToSpeechContent(BriefingEntity briefingEntity) {
		return new TextToSpeechContent(
			briefingEntity.getId(),
			briefingEntity.getSummaryText(),
			briefingEntity.getQuizEntities().stream()
				.map(
					quizEntity -> new TextToSpeechContent.textToSpeechQuiz(quizEntity.getId(), quizEntity.getProblem()))
				.toList()
		);
	}

	public EmptyBriefingTts toEmptyBriefingTts(BriefingEntity briefingEntity) {
		return new EmptyBriefingTts(briefingEntity.getId(), briefingEntity.getWakeUpBriefingContent());
	}

	public LatestBriefing toLatestBriefing(BriefingEntity briefingEntity) {
		return new LatestBriefing(briefingEntity.getId(), briefingEntity.getCreatedAt());
	}
}
