package com.sf.honeymorning.brief.adapter.in.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.validation.annotation.Validated;

import com.sf.honeymorning.brief.adapter.in.event.dto.BriefingSearchCommandDto;
import com.sf.honeymorning.brief.application.port.in.SearchCommandUseCase;
import com.sf.honeymorning.common.event.compensation.aop.FailCompensation;

import jakarta.validation.Valid;

@Validated
@Component
public class SearchCommandListener {
	private final SearchCommandUseCase searchCommandUseCase;

	public SearchCommandListener(SearchCommandUseCase searchCommandUseCase) {
		this.searchCommandUseCase = searchCommandUseCase;
	}

	@Async("eventTaskExecutor")
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@EventListener(BriefingSearchCommandDto.class)
	@FailCompensation
	public void register(@Valid BriefingSearchCommandDto briefingSearchCommandDto) {
		searchCommandUseCase.register(briefingSearchCommandDto);
	}

}
