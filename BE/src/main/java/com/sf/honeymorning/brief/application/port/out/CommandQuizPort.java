package com.sf.honeymorning.brief.application.port.out;

import java.util.List;

import com.sf.honeymorning.brief.application.domain.EmptySelectionQuiz;

public interface CommandQuizPort {
	void reflect(List<EmptySelectionQuiz> filledSelectionQuizzes);
}
