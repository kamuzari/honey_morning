package com.sf.honeymorning.alarm.adapter.in.web.port.out;

import java.util.List;

import com.sf.honeymorning.alarm.adapter.in.web.dto.response.AlarmResultResponseDto;

public interface AlarmResultQueryPort {
	List<AlarmResultResponseDto> getMyAlarmResults(Long userId, Long lastId);

	int getMaximumStreak(Long userId);
}
