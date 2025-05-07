package com.sf.honeymorning.alarm.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.alarm.adapter.in.web.dto.response.AlarmTagResponseDto;
import com.sf.honeymorning.alarm.adapter.out.persistence.entity.AlarmTagEntity;

@Component
public class AlarmTagPersistenceMapper {
	public AlarmTagResponseDto toAlarmTagResponseDto(AlarmTagEntity alarmTagEntity) {
		return new AlarmTagResponseDto(
			alarmTagEntity.getId(),
			alarmTagEntity.getAlarmEntity().getId(),
			alarmTagEntity.getTagEntity().getId(),
			alarmTagEntity.getTagEntity().getWord()
		);
	}
}
