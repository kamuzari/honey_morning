package com.honeymorning.api.brief.application.port.out;

import java.util.List;

import com.honeymorning.api.brief.application.domain.EmptySelectionQuiz;

public interface CommandQuizPort {
	void reflect(List<EmptySelectionQuiz> filledSelectionQuizzes);
}
