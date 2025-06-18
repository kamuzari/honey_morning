package com.sf.honeymorning.brief.application.service;

import org.springframework.stereotype.Service;

import com.sf.honeymorning.brief.adapter.in.event.dto.BriefingSearchCommandDto;
import com.sf.honeymorning.brief.application.port.in.SearchCommandUseCase;
import com.sf.honeymorning.brief.application.port.out.CommandBriefingSearchPort;

@Service
public class BriefingSearchService implements SearchCommandUseCase {
	private final CommandBriefingSearchPort commandBriefingSearchPort;

	public BriefingSearchService(CommandBriefingSearchPort commandBriefingSearchPort) {
		this.commandBriefingSearchPort = commandBriefingSearchPort;
	}

	@Override
	public void register(BriefingSearchCommandDto searchCommandDto) {
		commandBriefingSearchPort.reflect(searchCommandDto.briefingId());
	}
}
