package com.sf.honeymorning.brief.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.brief.adapter.in.event.dto.BriefingSearchCommandDto;
import com.sf.honeymorning.brief.application.port.in.FallBackSearchCommandUseCase;
import com.sf.honeymorning.brief.application.port.in.SearchCommandUseCase;
import com.sf.honeymorning.brief.application.port.in.SearchDocumentFailCompensateUseCase;
import com.sf.honeymorning.brief.application.port.out.CommandBriefingDocumentPort;
import com.sf.honeymorning.brief.application.port.out.CommandFailSearchEventPort;
import com.sf.honeymorning.brief.application.port.out.SearchDocumentCompensationPort;

@Service
public class BriefingSearchService implements
	SearchCommandUseCase,
	FallBackSearchCommandUseCase{

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
