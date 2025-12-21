package com.honeymorning.relay.briefing.application.domain;

import java.util.List;

import com.honeymorning.common.common.content.Content;
import com.honeymorning.common.domain.briefing.constraint.QuizConstraint;

public class TextToSpeechContent {
	private final Long briefingId;
	private final String summaryText;
	private final List<textToSpeechQuiz> textToSpeechQuizzes;
	private Content content = null;

	public TextToSpeechContent(
		Long briefingId,
		String summaryText,
		List<textToSpeechQuiz> textToSpeechQuizzes) {
		validateTtsQuizzes(textToSpeechQuizzes);
		this.briefingId = briefingId;
		this.summaryText = summaryText;
		this.textToSpeechQuizzes = textToSpeechQuizzes;
	}

	public Long getBriefingId() {
		return briefingId;
	}

	public String getSummaryText() {
		return summaryText;
	}

	public List<textToSpeechQuiz> getTtsQuizzes() {
		return textToSpeechQuizzes;
	}

	public void addContent(Content content) {
		this.content = content;
	}

	public Content getContent() {
		return this.content;
	}

	private void validateTtsQuizzes(List<textToSpeechQuiz> textToSpeechQuizzes) {
		if (textToSpeechQuizzes == null || textToSpeechQuizzes.size() != QuizConstraint.TOTAL_QUIZ_SIZE) {
			throw new IllegalArgumentException("정확히 2개의 퀴즈를 포함해야 합니다.");
		}
	}

	public static class textToSpeechQuiz {
		private final Long id;
		private final String questionText;
		private Content content = null;

		public textToSpeechQuiz(
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
}
