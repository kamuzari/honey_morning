package com.honeymorning.relay.briefing.application.port.out;

import com.honeymorning.relay.briefing.application.domain.EmptyBriefingTts;
import com.honeymorning.relay.briefing.application.domain.LatestBriefing;
import com.honeymorning.relay.briefing.application.domain.TextToSpeechContent;
import com.honeymorning.relay.briefing.application.service.dto.AiResponseDto;

public interface CommandBriefingPort {
	Long create(AiResponseDto aiResponseDto);

	void reflect(TextToSpeechContent textToSpeechContent);

	EmptyBriefingTts getEmptyBriefingTts(Long userId);

	LatestBriefing getLatestBriefingId(Long userId);

	void reflect(EmptyBriefingTts emptyBriefingTts);
}
