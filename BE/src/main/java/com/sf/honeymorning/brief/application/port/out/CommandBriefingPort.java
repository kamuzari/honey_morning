package com.sf.honeymorning.brief.application.port.out;

import com.sf.honeymorning.alarm.application.service.dto.response.AiResponseDto;
import com.sf.honeymorning.brief.application.domain.TextToSpeechContent;

public interface CommandBriefingPort {
	Long create(AiResponseDto aiResponseDto);

	void reflect(TextToSpeechContent textToSpeechContent);
}
