package com.honeymorning.api.alarm.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.honeymorning.api.alarm.adapter.in.web.dto.response.AlarmTagResponseDto;
import com.honeymorning.common.domain.alarm.entity.AlarmTagEntity;

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
