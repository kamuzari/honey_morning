package com.sf.honeymorning.brief.adapter.in.event;

import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.validation.annotation.Validated;

import com.sf.honeymorning.brief.adapter.in.event.dto.BriefingSearchCommandDto;
import com.sf.honeymorning.brief.application.port.in.SearchCommandUseCase;

import jakarta.validation.Valid;

@Validated
@Component
public class SearchCommandListener {
	private final SearchCommandUseCase searchCommandUseCase;
	public SearchCommandListener(SearchCommandUseCase searchCommandUseCase) {
		this.searchCommandUseCase = searchCommandUseCase;
	}

	@Async("eventTaskExecutor")
	@Retryable(maxAttempts = 2, backoff = @Backoff(delay = 1000))
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@EventListener(BriefingSearchCommandDto.class)
	public void register(@Valid BriefingSearchCommandDto briefingSearchCommandDto) {
		try {
			searchCommandUseCase.register(briefingSearchCommandDto);
		} catch (Exception e) {
			// todo: 검색데이터 반영 실패 복구
		}
	}

}
