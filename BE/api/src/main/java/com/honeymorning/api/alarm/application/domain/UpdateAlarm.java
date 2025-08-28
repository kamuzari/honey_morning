package com.honeymorning.api.alarm.application.domain;

import java.time.LocalTime;

import lombok.Getter;

@Getter
public class UpdateAlarm {
	private Long id;
	private final Long userId;
	private LocalTime wakeUpTime;
	private Integer dayOfTheWeeks;
	private Integer repeatFrequency;
	private Integer repeatInterval;
	boolean isActive;

	public UpdateAlarm(
		Long id,
		Long userId,
		LocalTime wakeUpTime,
		Integer dayOfTheWeeks,
		Integer repeatFrequency,
		Integer repeatInterval,
		boolean isActive
	) {
		this.id = id;
		this.userId = userId;
		this.wakeUpTime = wakeUpTime;
		this.dayOfTheWeeks = dayOfTheWeeks;
		this.repeatFrequency = repeatFrequency;
		this.repeatInterval = repeatInterval;
		this.isActive = isActive;
	}

	UpdateAlarm(
		Long userId,
		LocalTime wakeUpTime,
		Integer dayOfTheWeeks,
		Integer repeatFrequency,
		Integer repeatInterval,
		boolean isActive) {

		this.userId = userId;
		this.wakeUpTime = wakeUpTime;
		this.dayOfTheWeeks = dayOfTheWeeks;
		this.repeatFrequency = repeatFrequency;
		this.repeatInterval = repeatInterval;
		this.isActive = isActive;
	}

	public static UpdateAlarm initialize(Long userId) {
		return new UpdateAlarm(
			userId,
			LocalTime.of(7, 0),
			0,
			0,
			0,
			false
		);
	}

	public void update(
		LocalTime alarmTime,
		Integer dayOfTheWeeks,
		Integer repeatFrequency,
		Integer repeatInterval,
		Boolean active) {

		this.wakeUpTime = alarmTime;
		this.dayOfTheWeeks = dayOfTheWeeks;
		this.repeatFrequency = repeatFrequency;
		this.repeatInterval = repeatInterval;
		this.isActive = active;
	}
}
