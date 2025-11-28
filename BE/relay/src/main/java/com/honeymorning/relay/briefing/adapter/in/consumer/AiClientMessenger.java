package com.honeymorning.relay.briefing.adapter.in.consumer;

import com.honeymorning.relay.briefing.application.port.in.AlarmContentCommandUseCase;
import com.honeymorning.relay.briefing.application.port.in.TextToSpeechCommandUseCase;
import com.honeymorning.relay.briefing.application.service.dto.AiResponseDto;

public abstract class AiClientMessenger {
	protected final AlarmContentCommandUseCase alarmContentCommandUseCase;
	protected final TextToSpeechCommandUseCase textToSpeechCommandUseCase;

	protected AiClientMessenger(
		AlarmContentCommandUseCase alarmContentCommandUseCase,
		TextToSpeechCommandUseCase textToSpeechCommandUseCase
	) {
		this.alarmContentCommandUseCase = alarmContentCommandUseCase;
		this.textToSpeechCommandUseCase = textToSpeechCommandUseCase;
	}

	protected void processStore(AiResponseDto response) {
		beforeProcess(response);
		alarmContentCommandUseCase.create(response);
		afterProcess(response);
	}

	protected void processBriefingTts(AiResponseDto response) {
		beforeProcess(response);
		textToSpeechCommandUseCase.createBriefingTts(response.userId(), response.aiBriefings().summaryContent());
		afterProcess(response);
	}

	protected void processQuiz1Tts(AiResponseDto response) {
		beforeProcess(response);
		textToSpeechCommandUseCase.createQuizTts(response.userId(), response.aiQuizzes().get(0).problem(), 1);
		afterProcess(response);
	}

	protected void processQuiz2Tts(AiResponseDto response) {
		beforeProcess(response);
		textToSpeechCommandUseCase.createQuizTts(response.userId(), response.aiQuizzes().get(1).problem(), 2);
		afterProcess(response);
	}

	protected void beforeProcess(AiResponseDto response) {
	}

	protected void afterProcess(AiResponseDto response) {
	}
}
