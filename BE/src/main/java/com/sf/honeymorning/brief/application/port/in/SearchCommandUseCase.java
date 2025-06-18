package com.sf.honeymorning.brief.application.port.in;

import com.sf.honeymorning.brief.adapter.in.event.dto.BriefingSearchCommandDto;

public interface SearchCommandUseCase {
	void register(BriefingSearchCommandDto searchCommandDto);
}
