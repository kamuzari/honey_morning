package com.honeymorning.relay.briefing.application.service;

import org.springframework.stereotype.Service;

import com.honeymorning.relay.briefing.application.port.in.SearchCommandUseCase;
import com.honeymorning.relay.briefing.application.port.out.CommandBriefingDocumentPort;

@Service
public class BriefingSearchService implements SearchCommandUseCase {
	private final CommandBriefingDocumentPort commandBriefingDocumentPort;

	public BriefingSearchService(
		CommandBriefingDocumentPort commandBriefingDocumentPort) {

		this.commandBriefingDocumentPort = commandBriefingDocumentPort;
	}

	public void register(Long briefingId) {
		commandBriefingDocumentPort.reflect(briefingId);
	}
}
