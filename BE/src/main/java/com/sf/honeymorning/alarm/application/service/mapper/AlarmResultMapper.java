package com.sf.honeymorning.alarm.application.service.mapper;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.alarm.adapter.in.web.dto.request.AddAlarmResultRequestDto;
import com.sf.honeymorning.alarm.application.domain.AddAlarmResult;

@Component
public class AlarmResultMapper {
	public AddAlarmResult toAddAlarmResult(Long userId, AddAlarmResultRequestDto requestDto) {
		return new AddAlarmResult(
			userId,
			requestDto.briefingId(),
			requestDto.matchCount(),
			true
		);
	}
}
