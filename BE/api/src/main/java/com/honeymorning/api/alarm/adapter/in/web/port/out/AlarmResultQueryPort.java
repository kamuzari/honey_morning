package com.honeymorning.api.alarm.adapter.in.web.port.out;

import java.util.List;

import com.honeymorning.api.alarm.adapter.in.web.dto.response.AlarmResultResponseDto;

public interface AlarmResultQueryPort {
	List<AlarmResultResponseDto> getMyAlarmResults(Long userId, Long lastId);

	int getMaximumStreak(Long userId);
}
