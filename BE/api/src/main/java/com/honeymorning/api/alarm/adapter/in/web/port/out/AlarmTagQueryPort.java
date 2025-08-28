package com.honeymorning.api.alarm.adapter.in.web.port.out;

import java.util.List;

import com.honeymorning.api.alarm.adapter.in.web.dto.response.AlarmTagResponseDto;

public interface AlarmTagQueryPort {
	List<AlarmTagResponseDto> getMyAlarmTags(Long userId);
}
