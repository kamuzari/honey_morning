package com.sf.honeymorning.alarm.application.domain;

import java.time.Duration;
import java.time.LocalDateTime;

import com.sf.honeymorning.alarm.adapter.out.persistence.entity.UserAlarmResultStreakEntity;

import lombok.Getter;

@Getter
public class CreateUserAlarmStreak {
	private Long userId;

	private LocalDateTime latestAt;

	private int consecutiveDays;

	public CreateUserAlarmStreak(Long userId, LocalDateTime latestAt, int consecutiveDays) {
		this.userId = userId;
		this.latestAt = latestAt;
		this.consecutiveDays = consecutiveDays;
	}

	public static UserAlarmResultStreakEntity initialize(Long userId) {
		return new UserAlarmResultStreakEntity(userId, LocalDateTime.now().minusMonths(1), 1);
	}

	public void countConsecutiveDays(LocalDateTime now) {
		if (isWithin24Hours(now)) {
			this.consecutiveDays++;
			this.latestAt = now;
			return;
		}

		this.latestAt = now;
		this.consecutiveDays = 1;
	}

	private boolean isWithin24Hours(LocalDateTime now) {
		long hours = Duration.between(latestAt, now).toHours();
		return hours < 24;
	}

}
