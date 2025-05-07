package com.sf.honeymorning.alarm.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.alarm.adapter.out.persistence.entity.UserAlarmResultStreakEntity;
import com.sf.honeymorning.alarm.application.domain.User;
import com.sf.honeymorning.alarm.application.domain.UserAlarmStreak;
import com.sf.honeymorning.user.adapter.out.persistence.entity.UserEntity;

@Component
public class AlarmStreakPersistenceMapper {
	public UserAlarmStreak toDomain(UserAlarmResultStreakEntity userAlarmResultStreakEntity) {
		return new UserAlarmStreak(
			userAlarmResultStreakEntity.getUserId(),
			userAlarmResultStreakEntity.getLatestAt(),
			userAlarmResultStreakEntity.getConsecutiveDays()
		);
	}

	public User toDomain(UserEntity userEntity) {
		return new User(
			userEntity.getId(),
			userEntity.getUsername(),
			userEntity.getPassword(),
			userEntity.getNickName(),
			userEntity.getMaxStreak(),
			userEntity.getRole()
		);
	}
}
