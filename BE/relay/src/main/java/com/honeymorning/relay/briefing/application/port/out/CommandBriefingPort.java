package com.honeymorning.relay.briefing.application.port.out;

import com.honeymorning.relay.briefing.application.domain.TextToSpeechContent;
import com.honeymorning.relay.briefing.application.service.dto.AiResponseDto;

public interface CommandBriefingPort {
	Long create(AiResponseDto aiResponseDto);

	void reflect(TextToSpeechContent textToSpeechContent);
}
