package com.sf.honeymorning.brief.application.port.out;

import java.util.List;

import com.sf.honeymorning.brief.application.domain.EmptySelectionQuiz;

public interface LoadQuizPort {
	List<EmptySelectionQuiz> getQuizzes(Long userId, Long briefingId, List<Long> quizIds);
}
