package com.honeymorning.relay.briefing.application.port.in;

import com.honeymorning.relay.briefing.adapter.in.event.dto.BriefingSearchCommandDto;

public interface SearchCommandUseCase {
	void register(BriefingSearchCommandDto searchCommandDto);
}
