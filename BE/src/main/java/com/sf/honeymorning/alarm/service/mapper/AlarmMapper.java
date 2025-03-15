package com.sf.honeymorning.alarm.service.mapper;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.alarm.controller.dto.response.AlarmResponse;
import com.sf.honeymorning.alarm.domain.entity.Alarm;

@Component
public class AlarmMapper {
	public AlarmResponse toAlarmResponse(Alarm alarm) {
		return new AlarmResponse(
			alarm.getId(),
			alarm.getWakeUpTime(),
			alarm.getDayOfTheWeeks(),
			alarm.getRepeatFrequency(),
			alarm.getRepeatInterval(),
			alarm.isActive()
		);
	}
}
