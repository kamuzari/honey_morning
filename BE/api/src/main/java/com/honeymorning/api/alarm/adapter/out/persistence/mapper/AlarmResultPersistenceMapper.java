package com.honeymorning.api.alarm.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.honeymorning.api.alarm.adapter.in.web.dto.response.AlarmResultResponseDto;
import com.honeymorning.common.domain.alarm.entity.AlarmResultEntity;

@Component
public class AlarmResultPersistenceMapper {
	public AlarmResultResponseDto toAlarmResultResponseDto(AlarmResultEntity alarmResultEntity) {
		return new AlarmResultResponseDto(
			alarmResultEntity.getId(),
			alarmResultEntity.getCount(),
			alarmResultEntity.isAttended(),
			alarmResultEntity.getCreatedAt()
		);
	}
}
