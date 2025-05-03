package com.sf.honeymorning.brief.adapter.in.web.port.out;

import com.sf.honeymorning.brief.adapter.in.web.dto.response.BriefHistoryResponseDto;
import com.sf.honeymorning.brief.adapter.in.web.dto.response.BriefingDetailResponseDto;

public interface BriefingQueryPort {
	BriefHistoryResponseDto getMyBriefings(Long userId, int page);

	BriefingDetailResponseDto getMyBriefing(Long userId, Long briefId);
}
