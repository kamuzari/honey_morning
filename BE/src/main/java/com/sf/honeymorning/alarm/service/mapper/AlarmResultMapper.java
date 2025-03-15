package com.sf.honeymorning.alarm.service.mapper;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.alarm.controller.dto.response.AlarmResultResponseDto;
import com.sf.honeymorning.alarm.domain.entity.AlarmResult;

@Component
public class AlarmResultMapper {
	public AlarmResultResponseDto toAlarmResultResponseDto(AlarmResult alarmResult) {
		return new AlarmResultResponseDto(
			alarmResult.getId(),
			alarmResult.getCount(),
			alarmResult.isAttended(),
			alarmResult.getCreatedAt()
		);
	}
}
