package com.sf.honeymorning.alarm.application.port.in;

import com.sf.honeymorning.alarm.adapter.in.web.dto.request.AlarmSetRequest;

public interface AlarmCommandUseCase {
	void update(AlarmSetRequest alarmRequestDto, Long userId);
}
