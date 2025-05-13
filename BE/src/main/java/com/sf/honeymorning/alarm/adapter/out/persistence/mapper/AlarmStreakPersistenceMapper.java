package com.sf.honeymorning.alarm.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.alarm.adapter.out.persistence.entity.UserAlarmResultStreakEntity;
import com.sf.honeymorning.alarm.application.domain.CreateUserAlarmStreak;
import com.sf.honeymorning.alarm.application.domain.UpdateStreakUser;
import com.sf.honeymorning.user.adapter.out.persistence.entity.UserEntity;

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
