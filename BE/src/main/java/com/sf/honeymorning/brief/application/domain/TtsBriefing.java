package com.sf.honeymorning.brief.application.domain;

import java.util.List;

import com.sf.honeymorning.common.entity.content.Content;

public class TtsBriefing {
	private final Long id;
	private final String summaryText;
	private final List<TtsQuiz> ttsQuizzes;
	private Content content = null;

	public TtsBriefing(
		Long id,
		String summaryText,
		List<TtsQuiz> ttsQuizzes) {
		validateTtsQuizzes(ttsQuizzes);
		this.id = id;
		this.summaryText = summaryText;
		this.ttsQuizzes = ttsQuizzes;
	}

	public Long getId() {
		return id;
	}

	public String getSummaryText() {
		return summaryText;
	}

	public List<TtsQuiz> getTtsQuizzes() {
		return ttsQuizzes;
	}

	public void addContent(Content content) {
		this.content = content;
	}

	public Content getContent() {
		return this.content;
	}

	public static class TtsQuiz {
		private final Long id;
		private final String questionText;
		private Content content = null;

		public TtsQuiz(
			Long id,
			String questionText
		) {

			this.id = id;
			this.questionText = questionText;
			this.content = null;
		}

		public Long getId() {
			return id;
		}

		public String getQuestionText() {
			return questionText;
		}

		public void addContent(Content content) {
			this.content = content;
		}

		public Content getContent() {
			return this.content;
		}
	}

	private void validateTtsQuizzes(List<TtsQuiz> ttsQuizzes) {
		if (ttsQuizzes == null || ttsQuizzes.size() != 2) {
			throw new IllegalArgumentException("ttsQuizzes는 정확히 2개의 퀴즈를 포함해야 합니다.");
		}
	}
}
