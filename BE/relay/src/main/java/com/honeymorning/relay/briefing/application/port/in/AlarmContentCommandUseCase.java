package com.honeymorning.relay.briefing.application.port.in;

import com.honeymorning.relay.briefing.application.service.dto.AiResponseDto;

public interface AlarmContentCommandUseCase {
	void create(AiResponseDto aiResponseDto);
}
