package com.honeymorning.relay.briefing.adapter.in.consumer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.honeymorning.relay.briefing.application.port.in.AlarmContentCommandUseCase;
import com.honeymorning.relay.briefing.application.port.in.TextToSpeechCommandUseCase;
import com.honeymorning.relay.briefing.application.service.dto.AiResponseDto;
import com.honeymorning.relay.config.messaging.rabbitmq.constant.FromAiQueue;
import com.rabbitmq.client.Channel;

@Component
public class AiClientConsumer {
	private static final Logger log = LoggerFactory.getLogger(AiClientConsumer.class);

	private final AlarmContentCommandUseCase alarmContentCommandUseCase;
	private final TextToSpeechCommandUseCase textToSpeechCommandUseCase;

	public AiClientConsumer(
		AlarmContentCommandUseCase alarmContentCommandUseCase,
		TextToSpeechCommandUseCase textToSpeechCommandUseCase
	) {
		this.alarmContentCommandUseCase = alarmContentCommandUseCase;
		this.textToSpeechCommandUseCase = textToSpeechCommandUseCase;
	}

	@RabbitListener(queues = FromAiQueue.NAME, ackMode = "MANUAL")
	public void createAlarmContents(
		AiResponseDto response,
		Channel channel,
		@Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {

		try {
			alarmContentCommandUseCase.create(response);
			channel.basicAck(tag, false);
		} catch (Exception e) {
			log.error("fallback fail : {}, {}", e.getMessage(), e);
			channel.basicNack(tag, false, false);
		}
	}

	@KafkaListener(
		topics = "${app.kafka.consumers.ai-store.topic}",
		groupId = "${app.kafka.consumers.ai-store.group-id}"
	)
	public void storeAiResponse(AiResponseDto response) {
		alarmContentCommandUseCase.create(response);
	}

	@KafkaListener(
		topics = "${app.kafka.consumers.ai-briefing-tts.topic}",
		groupId = "${app.kafka.consumers.ai-briefing-tts.group-id}"
	)
	public void createBriefingTts(AiResponseDto response) {
		textToSpeechCommandUseCase.createBriefing(response.userId(), response.aiBriefings().summaryContent());
	}

	@KafkaListener(
		topics = "${app.kafka.consumers.ai-quiz1-tts.topic}",
		groupId = "${app.kafka.consumers.ai-quiz1-tts.group-id}"
	)
	public void createQuiz1Tts(AiResponseDto response) {
		textToSpeechCommandUseCase.createQuizTts(response.userId(), response.aiQuizzes().get(0).problem(), 1);
	}

	@KafkaListener(
		topics = "${app.kafka.consumers.ai-quiz2-tts.topic}",
		groupId = "${app.kafka.consumers.ai-quiz2-tts.group-id}"
	)
	public void createQuiz2Tts(AiResponseDto response) {
		textToSpeechCommandUseCase.createQuizTts(response.userId(), response.aiQuizzes().get(1).problem(), 2);
	}
}
