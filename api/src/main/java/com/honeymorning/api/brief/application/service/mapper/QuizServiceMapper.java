package com.honeymorning.api.brief.application.service.mapper;

import org.springframework.stereotype.Component;

import com.honeymorning.api.brief.adapter.in.web.dto.request.SelectionRequestDto;
import com.honeymorning.api.brief.application.domain.SolveQuiz;

@Component
public class QuizServiceMapper {
	public SolveQuiz toSolveQuiz(SelectionRequestDto.SelectionQuizDto requestQuiz) {
		return new SolveQuiz(requestQuiz.quizId(), requestQuiz.selection());
	}
}
