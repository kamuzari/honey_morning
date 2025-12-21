package com.honeymorning.relay.briefing.application.port.in;

public interface TextToSpeechCommandUseCase {
	void create(Long briefingId);

	void createBriefingTts(Long userId, String summaryText);

	void createQuizTts(Long userId, String quizText, Integer order);
}
