package com.honeymorning.api.brief.application.port.in;

import com.honeymorning.api.brief.adapter.in.event.dto.BriefingSearchCommandDto;

public interface SearchCommandUseCase {
	void register(BriefingSearchCommandDto searchCommandDto);
}
