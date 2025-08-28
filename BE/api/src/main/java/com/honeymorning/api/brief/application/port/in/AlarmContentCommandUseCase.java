package com.honeymorning.api.brief.application.port.in;

import com.honeymorning.api.alarm.application.service.dto.response.AiResponseDto;

public interface AlarmContentCommandUseCase {
	void create(AiResponseDto aiResponseDto);
}
