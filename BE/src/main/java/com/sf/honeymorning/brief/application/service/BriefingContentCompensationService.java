package com.sf.honeymorning.brief.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.brief.application.domain.RetryingFailTts;
import com.sf.honeymorning.brief.application.port.in.TtsFailFallbackUseCase;
import com.sf.honeymorning.brief.application.port.out.TtsCompensationPort;

@Service
public class BriefingContentCompensationService implements TtsFailFallbackUseCase {
	private final TtsCompensationPort ttsCompensationPort;
	private final TextToSpeechGenerateService textToSpeechGenerateService;

	public BriefingContentCompensationService(TtsCompensationPort ttsCompensationPort,
		TextToSpeechGenerateService textToSpeechGenerateService) {
		this.ttsCompensationPort = ttsCompensationPort;
		this.textToSpeechGenerateService = textToSpeechGenerateService;
	}

	@Transactional(transactionManager = "eventTransactionManager", timeout = 3)
	public void retryTts() {
		RetryingFailTts retryingFailTts = ttsCompensationPort.loadTopOnSkipLock();
		if (retryingFailTts.isEmpty()) {
			return;
		}

		textToSpeechGenerateService.create(retryingFailTts.getFailBriefingId());
		retryingFailTts.complete();
		ttsCompensationPort.reflect(retryingFailTts);
	}
}
// 2025-08-03 19:32:32.031 [           main] DEBUG [] [] org.hibernate.SQL - create table fail_tts_events (id bigint not null auto_increment, briefing_id bigint not null, event_status enum ('FAILED','RETRY_COMPLETED'), primary key (id)) engine=InnoDB
