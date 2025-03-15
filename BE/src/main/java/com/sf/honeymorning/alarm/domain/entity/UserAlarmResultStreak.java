package com.sf.honeymorning.alarm.domain.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;

@Getter
@RedisHash("userAlarmResultStreak")
public class UserAlarmResultStreak {

	@Id
	private Long userId;

	private LocalDateTime latestAt;

	private int consecutiveDays;

	protected UserAlarmResultStreak() {
	}

	public UserAlarmResultStreak(Long userId, LocalDateTime latestAt, int consecutiveDays) {
		this.userId = userId;
		this.latestAt = latestAt;
		this.consecutiveDays = consecutiveDays;
	}

	public static UserAlarmResultStreak initialize(Long userId) {
		return new UserAlarmResultStreak(userId, LocalDateTime.now().minusMonths(1), 1);
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
		return Duration.between(latestAt, now).toHours() < 24;
	}
}
