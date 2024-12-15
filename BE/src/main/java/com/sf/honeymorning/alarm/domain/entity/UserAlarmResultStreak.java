package com.sf.honeymorning.alarm.domain.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("userAlarmResultStreak")
public class UserAlarmResultStreak {

	@Id
	private Long userId;

	private LocalDateTime createdAt;

	private int consecutiveDays;

	protected UserAlarmResultStreak() {
	}

	public UserAlarmResultStreak(Long userId, LocalDateTime createdAt, int consecutiveDays) {
		this.userId = userId;
		this.createdAt = createdAt;
		this.consecutiveDays = consecutiveDays;
	}

	public Long getUserId() {
		return userId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public int getConsecutiveDays() {
		return consecutiveDays;
	}

	public void countConsecutiveDays(LocalDateTime now) {
		if (isWithin24Hours(now)) {
			this.consecutiveDays++;
			this.createdAt = now;
			return;
		}

		this.createdAt = now;
		this.consecutiveDays = 1;
	}

	private boolean isWithin24Hours(LocalDateTime now) {
		return Duration.between(createdAt, now).toHours() < 24;
	}
}
