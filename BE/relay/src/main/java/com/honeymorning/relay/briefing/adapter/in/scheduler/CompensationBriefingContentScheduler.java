package com.honeymorning.relay.briefing.adapter.in.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.honeymorning.relay.briefing.application.port.in.SearchDocumentFailCompensateUseCase;
import com.honeymorning.relay.briefing.application.port.in.TtsFailCompensateUseCase;

@Profile({"local", "prod"})
@Component
public class CompensationBriefingContentScheduler {
	private static final Logger log = LoggerFactory.getLogger(CompensationBriefingContentScheduler.class);
	private static final String EVERY_PER_SECONDS = "* * * * * *";

	private final TtsFailCompensateUseCase ttsFailCompensateUseCase;
	private final SearchDocumentFailCompensateUseCase searchDocumentFailCompensateUseCase;

	public CompensationBriefingContentScheduler(
		TtsFailCompensateUseCase ttsFailCompensateUseCase,
		SearchDocumentFailCompensateUseCase searchDocumentFailCompensateUseCase
	) {
		this.ttsFailCompensateUseCase = ttsFailCompensateUseCase;
		this.searchDocumentFailCompensateUseCase = searchDocumentFailCompensateUseCase;
	}

	@Scheduled(cron = EVERY_PER_SECONDS)
	public void compensateTts() {
		log.info("CompensationScheduler fallbackTts started");
		ttsFailCompensateUseCase.retryTts();
	}

	@Scheduled(cron = EVERY_PER_SECONDS)
	public void compensateSearchDocument() {
		log.info("CompensationScheduler fallbackSearchDocument started");
		searchDocumentFailCompensateUseCase.retrySearchDocument();
	}
}