package com.honeymorning.api.brief.application.port.out;

import java.util.List;

import com.honeymorning.api.brief.application.domain.EmptySelectionQuiz;

public interface LoadQuizPort {
	List<EmptySelectionQuiz> getQuizzes(Long userId, Long briefingId, List<Long> quizIds);
}
