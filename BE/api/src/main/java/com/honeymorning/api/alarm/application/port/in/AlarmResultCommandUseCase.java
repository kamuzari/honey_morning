package com.honeymorning.api.alarm.application.port.in;

import com.honeymorning.api.alarm.adapter.in.web.dto.request.AddAlarmResultRequestDto;

public interface AlarmResultCommandUseCase {
	void add(Long userId, AddAlarmResultRequestDto requestDto);
}
