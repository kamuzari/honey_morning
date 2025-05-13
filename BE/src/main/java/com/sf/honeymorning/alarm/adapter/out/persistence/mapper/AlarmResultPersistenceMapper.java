package com.sf.honeymorning.alarm.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.alarm.adapter.in.web.dto.response.AlarmResultResponseDto;
import com.sf.honeymorning.alarm.adapter.out.persistence.entity.AlarmResultEntity;

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
