package com.sf.honeymorning.brief.adapter.in.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.validation.annotation.Validated;

import com.sf.honeymorning.brief.adapter.in.event.dto.BriefingTtsCommandDto;
import com.sf.honeymorning.brief.application.port.in.TextToSpeechCommandUseCase;
import com.sf.honeymorning.common.event.compensation.aop.FailCompensation;

import jakarta.validation.Valid;

@Validated
@Component
public class TtsGenerateListener {
	private final TextToSpeechCommandUseCase textToSpeechCommandUseCase;

	public TtsGenerateListener(
		TextToSpeechCommandUseCase textToSpeechCommandUseCase) {
		this.textToSpeechCommandUseCase = textToSpeechCommandUseCase;
	}

	@Async("eventTaskExecutor")
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@EventListener(BriefingTtsCommandDto.class)
	@FailCompensation
	public void generate(@Valid BriefingTtsCommandDto briefingTtsCommandDto) {
		textToSpeechCommandUseCase.create(briefingTtsCommandDto.briefingId());
	}

}
