package com.sf.honeymorning.alarm.service.mapper;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.alarm.controller.dto.response.AlarmTagResponseDto;
import com.sf.honeymorning.alarm.domain.entity.AlarmTag;

@Component
public class AlarmTagMapper {
	public AlarmTagResponseDto toAlarmTagResponseDto(AlarmTag alarmTag) {
		return new AlarmTagResponseDto(
			alarmTag.getId(),
			alarmTag.getAlarm().getId(),
			alarmTag.getTag().getId(),
			alarmTag.getTag().getWord()
		);
	}
}
