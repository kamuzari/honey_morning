package com.honeymorning.relay.briefing.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.honeymorning.common.domain.briefing.entity.QuizEntity;
import com.honeymorning.relay.briefing.application.domain.EmptyQuizTts;

@Component
public class QuizPersistenceMapper {
	public EmptyQuizTts toEmptyQuizTts(QuizEntity quizEntity) {
		return new EmptyQuizTts(quizEntity.getId(), quizEntity.getWakeUpQuizContent());
	}
}
