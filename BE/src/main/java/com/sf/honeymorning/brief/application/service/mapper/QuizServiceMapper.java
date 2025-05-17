package com.sf.honeymorning.brief.application.service.mapper;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.brief.adapter.in.web.dto.request.SelectionRequestDto;
import com.sf.honeymorning.brief.application.domain.SolveQuiz;

@Component
public class QuizServiceMapper {
	public SolveQuiz toSolveQuiz(SelectionRequestDto.SelectionQuizDto requestQuiz) {
		return new SolveQuiz(requestQuiz.quizId(), requestQuiz.selection());
	}
}
