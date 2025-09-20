package com.honeymorning.relay.briefing.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.honeymorning.relay.briefing.adapter.in.event.dto.BriefingSearchCommandDto;
import com.honeymorning.relay.briefing.application.port.in.FallBackSearchCommandUseCase;
import com.honeymorning.relay.briefing.application.port.in.SearchCommandUseCase;
import com.honeymorning.relay.briefing.application.port.out.CommandBriefingDocumentPort;
import com.honeymorning.relay.briefing.application.port.out.CommandFailSearchEventPort;
import com.honeymorning.relay.briefing.application.port.out.SearchDocumentCompensationPort;

@Service
public class BriefingSearchService implements
	SearchCommandUseCase,
	FallBackSearchCommandUseCase {

	private final CommandBriefingDocumentPort commandBriefingDocumentPort;
	private final CommandFailSearchEventPort commandFailSearchEventPort;
	private final SearchDocumentCompensationPort searchDocumentCompensationPort;

	public BriefingSearchService(
		CommandBriefingDocumentPort commandBriefingDocumentPort,
		CommandFailSearchEventPort commandFailSearchEventPort,
		SearchDocumentCompensationPort searchDocumentCompensationPort) {

		this.commandBriefingDocumentPort = commandBriefingDocumentPort;
		this.commandFailSearchEventPort = commandFailSearchEventPort;
		this.searchDocumentCompensationPort = searchDocumentCompensationPort;
	}

	public void register(BriefingSearchCommandDto searchCommandDto) {
		commandBriefingDocumentPort.reflect(searchCommandDto.briefingId());
	}

	public void register(Long briefingId) {
		commandBriefingDocumentPort.reflect(briefingId);
	}

	@Transactional(transactionManager = "eventTransactionManager")
	public void write(Long briefingId) {
		commandFailSearchEventPort.save(briefingId);
	}
}
