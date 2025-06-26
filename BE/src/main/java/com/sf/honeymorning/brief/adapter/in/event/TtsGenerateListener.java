package com.sf.honeymorning.brief.adapter.in.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.validation.annotation.Validated;

import com.sf.honeymorning.brief.adapter.in.event.dto.BriefingTtsCommandDto;
import com.sf.honeymorning.brief.application.port.in.TextToSpeechCommandUseCase;
import com.sf.honeymorning.brief.application.service.TextToSpeechGenerateService;

import jakarta.validation.Valid;

@Validated
@Component
public class TtsGenerateListener {
	private static final Logger log = LoggerFactory.getLogger(TtsGenerateListener.class);

	private final TextToSpeechCommandUseCase textToSpeechCommandUseCase;

	public TtsGenerateListener(TextToSpeechGenerateService ttsCommandUseCase) {
		this.textToSpeechCommandUseCase = ttsCommandUseCase;
	}

	@Async("eventTaskExecutor")
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@EventListener(Long.class)
	public void generate(@Valid BriefingTtsCommandDto briefingTtsCommandDto) {
		try {
			textToSpeechCommandUseCase.create(briefingTtsCommandDto.briefingId());
		} catch (Exception e) {
			log.warn("fail tts create fallback : {}", e.getMessage(), e);
			textToSpeechCommandUseCase.fallbackCompensationEvent(briefingTtsCommandDto.briefingId());
		}
	}

}
