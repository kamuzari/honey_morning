package com.sf.honeymorning.brief.adapter.in.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sf.honeymorning.brief.application.port.in.TtsFailFallbackUseCase;

@Component
public class CompensationBriefingContentScheduler {
	private static final Logger log = LoggerFactory.getLogger(CompensationBriefingContentScheduler.class);
	private static final String EVERY_PER_SECONDS = "* * * * * *";

	private final TtsFailFallbackUseCase ttsFailFallbackUseCase;

	public CompensationBriefingContentScheduler(TtsFailFallbackUseCase ttsFailFallbackUseCase) {
		this.ttsFailFallbackUseCase = ttsFailFallbackUseCase;
	}

	@Scheduled(cron = EVERY_PER_SECONDS)
	public void fallbackTts() {
		log.info("CompensationScheduler fallbackTts started");
		ttsFailFallbackUseCase.retryTts();
	}
}