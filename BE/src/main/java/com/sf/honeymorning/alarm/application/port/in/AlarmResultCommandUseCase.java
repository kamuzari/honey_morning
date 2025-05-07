package com.sf.honeymorning.alarm.application.port.in;

import com.sf.honeymorning.alarm.adapter.in.web.dto.request.AddAlarmResultRequestDto;

public interface AlarmResultCommandUseCase {
	void add(Long userId, AddAlarmResultRequestDto requestDto);
}
