package com.honeymorning.api.alarm.application.domain;

import static com.honeymorning.common.domain.alarm.constraint.AlarmConstraint.SLEEP_MODE_INTERVAL_CONDITION;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.honeymorning.common.domain.alarm.entity.DayOfTheWeek;

public record VerifySleepModeAlarm(
	LocalTime wakeUpTime,
	Integer dayOfTheWeeks,
	boolean isActive) {

	public boolean canSleepMode(LocalDateTime now) {
		DayOfTheWeek todayOfTheWeek = DayOfTheWeek.getDayOfWeek(
			now.plusHours(SLEEP_MODE_INTERVAL_CONDITION)
				.toLocalDate()
				.getDayOfWeek()
				.name()
		);

		boolean isMoreThan5Hours = getAlamReadyTime().isAfter(now.toLocalTime().minusMinutes(1));
		boolean isActiveDayOfWeeks = (this.dayOfTheWeeks & todayOfTheWeek.getShiftedBit()) > 0;

		return isActive && isMoreThan5Hours && isActiveDayOfWeeks;
	}

	private LocalTime getAlamReadyTime() {
		return this.wakeUpTime.minusHours(SLEEP_MODE_INTERVAL_CONDITION);
	}
}
