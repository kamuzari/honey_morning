package com.honeymorning.relay.briefing.adapter.in.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.honeymorning.relay.briefing.application.port.in.AlarmContentCommandUseCase;
import com.honeymorning.relay.briefing.application.port.in.TextToSpeechCommandUseCase;
import com.honeymorning.relay.briefing.application.service.dto.AiResponseDto;

@Component
public class AiClientConsumer extends AiClientMessenger {
	private static final Logger log = LoggerFactory.getLogger(AiClientConsumer.class);

	public AiClientConsumer(
		AlarmContentCommandUseCase alarmContentCommandUseCase,
		TextToSpeechCommandUseCase textToSpeechCommandUseCase
	) {
		super(alarmContentCommandUseCase, textToSpeechCommandUseCase);
	}

	@KafkaListener(
		topics = "${app.kafka.consumers.ai-store.topic}",
		groupId = "${app.kafka.consumers.ai-store.group-id}",
		containerFactory = "kafkaListenerContainerFactory"
	)
	public void storeAiResponse(AiResponseDto response) {
		processStore(response);
	}

	@KafkaListener(
		topics = "${app.kafka.consumers.ai-briefing-tts.topic}",
		groupId = "${app.kafka.consumers.ai-briefing-tts.group-id}",
		containerFactory = "kafkaListenerContainerFactory"
	)
	public void createBriefingTts(AiResponseDto response) {
		processBriefingTts(response);
	}

	@KafkaListener(
		topics = "${app.kafka.consumers.ai-quiz1-tts.topic}",
		groupId = "${app.kafka.consumers.ai-quiz1-tts.group-id}",
		containerFactory = "kafkaListenerContainerFactory"
	)
	public void createQuiz1Tts(AiResponseDto response) {
		processQuiz1Tts(response);
	}

	@KafkaListener(
		topics = "${app.kafka.consumers.ai-quiz2-tts.topic}",
		groupId = "${app.kafka.consumers.ai-quiz2-tts.group-id}",
		containerFactory = "kafkaListenerContainerFactory"
	)
	public void createQuiz2Tts(AiResponseDto response) {
		processQuiz2Tts(response);
	}

}
