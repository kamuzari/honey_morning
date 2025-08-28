package com.honeymorning.api.brief.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.honeymorning.api.brief.adapter.in.web.dto.request.SelectionRequestDto;
import com.honeymorning.api.brief.application.port.in.QuizCommandUseCase;
import com.honeymorning.api.brief.application.port.out.CommandQuizPort;
import com.honeymorning.api.brief.application.port.out.LoadQuizPort;
import com.honeymorning.api.brief.application.service.mapper.QuizServiceMapper;

@Transactional(readOnly = true)
@Service
public class QuizService implements QuizCommandUseCase {
	private final CommandQuizPort commandQuizPort;
	private final LoadQuizPort loadQuizPort;
	private final QuizServiceMapper quizServiceMapper;

	public QuizService(
		CommandQuizPort commandQuizPort,
		LoadQuizPort loadQuizPort,
		QuizServiceMapper quizServiceMapper) {

		this.commandQuizPort = commandQuizPort;
		this.loadQuizPort = loadQuizPort;
		this.quizServiceMapper = quizServiceMapper;
	}

	@Transactional
	public void solve(Long userId, SelectionRequestDto selectionRequestDto) {
		List<Long> quizIds = selectionRequestDto.selectionQuizDtos()
			.stream()
			.map(SelectionRequestDto.SelectionQuizDto::quizId)
			.toList();

		var emptySelectionQuizzes = loadQuizPort.getQuizzes(userId, selectionRequestDto.briefingId(), quizIds);

		selectionRequestDto.selectionQuizDtos().forEach(
			selectionQuizDto ->
				emptySelectionQuizzes.stream()
					.filter(emptySelectionQuiz -> emptySelectionQuiz.getQuizId().equals(selectionQuizDto.quizId()))
					.findFirst()
					.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 퀴즈입니다."))
					.solve(selectionQuizDto.selection())

		);
		commandQuizPort.reflect(emptySelectionQuizzes);
	}

}



