package com.honeymorning.api.brief.application.domain;

import static com.honeymorning.common.domain.briefing.constraint.QuizConstraint.ANSWER_MAXIMUM_VALUE;
import static com.honeymorning.common.domain.briefing.constraint.QuizConstraint.ANSWER_MINIMUM_VALUE;

public class EmptySelectionQuiz {
	Long quizId;
	Integer selection = null;

	public Long getQuizId() {
		return quizId;
	}

	public EmptySelectionQuiz(Long quizId, Integer selection) {
		this.quizId = quizId;
		if (selection != null) {
			throw new IllegalStateException(" 선택사항은 null 이어야 합니다.");
		}
	}

	public void solve(Integer selection) {
		if (selection < ANSWER_MINIMUM_VALUE || selection > ANSWER_MAXIMUM_VALUE) {
			throw new IllegalArgumentException("선택사항은 [1-4] 번까지만 유효합니다.");
		}

		this.selection = selection;
	}

	public Integer getSelection() {
		if (selection == null) {
			throw new IllegalStateException("답안 선택이 되지 않았습니다.");
		}

		return selection;
	}
}
