package com.honeymorning.api.brief.application.port.out;

import com.honeymorning.api.alarm.application.service.dto.response.AiResponseDto;
import com.honeymorning.api.brief.application.domain.TextToSpeechContent;

public interface CommandBriefingPort {
	Long create(AiResponseDto aiResponseDto);

	void reflect(TextToSpeechContent textToSpeechContent);
}
