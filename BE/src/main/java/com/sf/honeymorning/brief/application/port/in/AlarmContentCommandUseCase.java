package com.sf.honeymorning.brief.application.port.in;

import com.sf.honeymorning.alarm.application.service.dto.response.AiResponseDto;

public interface AlarmContentCommandUseCase {
	void create(AiResponseDto aiResponseDto);
}
