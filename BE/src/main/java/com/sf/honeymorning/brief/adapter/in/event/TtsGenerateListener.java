package com.sf.honeymorning.brief.adapter.in.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.validation.annotation.Validated;

import com.sf.honeymorning.brief.application.port.in.TtsCommandUseCase;
import com.sf.honeymorning.brief.application.service.TtsGenerateService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Validated
@Component
public class TtsGenerateListener {
	private final TtsCommandUseCase ttsCommandUseCase;

	public TtsGenerateListener(TtsGenerateService ttsCommandUseCase) {
		this.ttsCommandUseCase = ttsCommandUseCase;
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@EventListener(Long.class)
	public void generate(@Valid @NotNull @Positive Long briefingId) {
		ttsCommandUseCase.create(briefingId);
	}

}
