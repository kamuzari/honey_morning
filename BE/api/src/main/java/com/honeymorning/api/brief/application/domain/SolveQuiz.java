package com.honeymorning.api.brief.application.domain;

import static com.honeymorning.api.brief.common.QuizConstraint.ANSWER_MAXIMUM_VALUE;
import static com.honeymorning.api.brief.common.QuizConstraint.ANSWER_MINIMUM_VALUE;

public record SolveQuiz(Long quizId, Integer selection) {
	public SolveQuiz {
		if (selection < ANSWER_MINIMUM_VALUE || selection > ANSWER_MAXIMUM_VALUE) {
			throw new IllegalArgumentException("선택사항은 [1-4] 번까지만 유효합니다.");
		}
	}
}
