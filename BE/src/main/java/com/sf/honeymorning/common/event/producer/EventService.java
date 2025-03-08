package com.sf.honeymorning.common.event.producer;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.sf.honeymorning.alarm.service.TtsService;

@Service
public class EventService {
	private final TtsService ttsService;

	public EventService(TtsService ttsService) {
		this.ttsService = ttsService;
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@EventListener(Long.class)
	public void handleTts(Long briefingId) {
		ttsService.create(briefingId);
	}

}
