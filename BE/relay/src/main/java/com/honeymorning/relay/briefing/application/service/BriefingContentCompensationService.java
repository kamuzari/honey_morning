package com.honeymorning.relay.briefing.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.honeymorning.relay.briefing.application.domain.RetryingFailTts;
import com.honeymorning.relay.briefing.application.port.in.SearchDocumentFailCompensateUseCase;
import com.honeymorning.relay.briefing.application.port.in.TtsFailCompensateUseCase;
import com.honeymorning.relay.briefing.application.port.out.SearchDocumentCompensationPort;
import com.honeymorning.relay.briefing.application.port.out.TtsCompensationPort;

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

	public void retryTts() {
		/**
		 * 0. 트랜잭션 시작
		 * 1. db에서 가장 오래된 실패한 이벤트 가져오기
		 * 2. 새로운 트랜잭션(required new) 새로운 tts 생성 시도(외부 api 호출 -> s3 업로드)
		 * 3. 1번에서 가져온 이벤트 상태변경
		 * 4. 커밋
 		 */

		RetryingFailTts retryingFailTts = ttsCompensationPort.loadTopOnSkipLock();
		if (retryingFailTts.isEmpty()) {
			return;
		}

		// 아래 creete 메소드에서 새로운 트랜잭션 이루어짐 ..
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
