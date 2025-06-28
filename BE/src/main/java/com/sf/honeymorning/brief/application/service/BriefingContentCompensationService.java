package com.sf.honeymorning.brief.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.brief.application.domain.RetryingFailTts;
import com.sf.honeymorning.brief.application.port.in.SearchDocumentFailCompensateUseCase;
import com.sf.honeymorning.brief.application.port.in.TtsFailCompensateUseCase;
import com.sf.honeymorning.brief.application.port.out.SearchDocumentCompensationPort;
import com.sf.honeymorning.brief.application.port.out.TtsCompensationPort;

@Service
public class BriefingContentCompensationService implements TtsFailCompensateUseCase,
	SearchDocumentFailCompensateUseCase {
	private final TtsCompensationPort ttsCompensationPort;
	private final TextToSpeechGenerateService textToSpeechGenerateService;

	private final SearchDocumentCompensationPort searchDocumentCompensationPort;
	private final BriefingSearchService briefingSearchService;

	public BriefingContentCompensationService(
		TtsCompensationPort ttsCompensationPort,
		TextToSpeechGenerateService textToSpeechGenerateService,
		SearchDocumentCompensationPort searchDocumentCompensationPort,
		BriefingSearchService briefingSearchService) {

		this.ttsCompensationPort = ttsCompensationPort;
		this.textToSpeechGenerateService = textToSpeechGenerateService;
		this.searchDocumentCompensationPort = searchDocumentCompensationPort;
		this.briefingSearchService = briefingSearchService;
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

	@Transactional(transactionManager = "eventTransactionManager", timeout = 3)
	public void retrySearchDocument() {
		var retryingFailSearchDocument = searchDocumentCompensationPort.loadTopOnSkipLock();
		if (retryingFailSearchDocument.isEmpty()) {
			return;
		}

		briefingSearchService.register(retryingFailSearchDocument.getBriefingId());
		retryingFailSearchDocument.complete();
		searchDocumentCompensationPort.reflect(retryingFailSearchDocument);
	}
}
