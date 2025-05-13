package com.sf.honeymorning.alarm.adapter.out.persistence.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;

@Getter
@RedisHash("userAlarmResultStreak")
public class UserAlarmResultStreakEntity {

	@Id
	private Long userId;

	private LocalDateTime latestAt;

	private int consecutiveDays;

	protected UserAlarmResultStreakEntity() {
	}

	public UserAlarmResultStreakEntity(Long userId, LocalDateTime latestAt, int consecutiveDays) {
		this.userId = userId;
		this.latestAt = latestAt;
		this.consecutiveDays = consecutiveDays;
	}

	// todo: 이게 과연 .. 도메인에 있어야 맞는거 아닌가..?
	public static UserAlarmResultStreakEntity initialize(Long userId) {
		return new UserAlarmResultStreakEntity(userId, LocalDateTime.now(), 0);
	}
}
