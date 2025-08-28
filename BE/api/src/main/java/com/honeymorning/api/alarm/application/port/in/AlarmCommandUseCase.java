package com.honeymorning.api.alarm.application.port.in;

import com.honeymorning.api.alarm.adapter.in.web.dto.request.AlarmSetRequest;

public interface AlarmCommandUseCase {
	void update(AlarmSetRequest alarmRequestDto, Long userId);
}
