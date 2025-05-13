package com.sf.honeymorning.alarm.application.domain;

import lombok.Getter;

@Getter
public class UpdateStreakUser {
	private Long id;
	private Integer maximumStreak;

	public UpdateStreakUser(Long id, Integer maximumStreak) {
		this.id = id;
		this.maximumStreak = maximumStreak;
	}

	public void updateMaximumStreak(int consecutiveDays) {
		if (this.maximumStreak < consecutiveDays) {
			this.maximumStreak = consecutiveDays;
		}
	}
}
