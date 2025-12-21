package com.honeymorning.api.brief.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.honeymorning.api.brief.adapter.in.web.dto.response.detail.QuizResponseDto;
import com.honeymorning.api.brief.application.domain.EmptySelectionQuiz;
import com.honeymorning.common.domain.briefing.entity.QuizEntity;

@Component
public class QuizPersistenceMapper {
	public QuizResponseDto toQuizResponseDto(QuizEntity quizEntity) {
		return new QuizResponseDto(
			quizEntity.getProblem(),
			quizEntity.getOption1(),
			quizEntity.getOption2(),
			quizEntity.getOption3(),
			quizEntity.getOption4(),
			quizEntity.getSelection(),
			quizEntity.getAnswer()
		);
	}

	public EmptySelectionQuiz toEmptySelectionQuiz(QuizEntity quizEntity) {
		return new EmptySelectionQuiz(
			quizEntity.getId(),
			quizEntity.getSelection()
		);
	}
}
