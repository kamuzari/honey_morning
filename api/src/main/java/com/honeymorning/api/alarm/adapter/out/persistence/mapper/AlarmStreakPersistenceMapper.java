package com.honeymorning.api.alarm.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.honeymorning.api.alarm.adapter.out.persistence.entity.UserAlarmResultStreakEntity;
import com.honeymorning.api.alarm.application.domain.CreateUserAlarmStreak;
import com.honeymorning.api.alarm.application.domain.UpdateStreakUser;
import com.honeymorning.common.domain.user.entity.UserEntity;

@Component
public class AlarmStreakPersistenceMapper {
	public CreateUserAlarmStreak toDomain(UserAlarmResultStreakEntity userAlarmResultStreakEntity) {
		return new CreateUserAlarmStreak(
			userAlarmResultStreakEntity.getUserId(),
			userAlarmResultStreakEntity.getLatestAt(),
			userAlarmResultStreakEntity.getConsecutiveDays()
		);
	}

	public UpdateStreakUser toDomain(UserEntity userEntity) {
		return new UpdateStreakUser(
			userEntity.getId(),
			userEntity.getMaxStreak()
		);
	}
}
