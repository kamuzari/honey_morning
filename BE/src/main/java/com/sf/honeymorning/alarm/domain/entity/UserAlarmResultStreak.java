package com.sf.honeymorning.alarm.domain.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

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

	public Long getUserId() {
		return userId;
	}

	public LocalDateTime getLatestAt() {
		return latestAt;
	}

	public int getConsecutiveDays() {
		return consecutiveDays;
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
