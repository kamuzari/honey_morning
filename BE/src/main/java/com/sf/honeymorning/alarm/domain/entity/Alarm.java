package com.sf.honeymorning.alarm.domain.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.sf.honeymorning.common.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "alarms")
@Entity
public class Alarm extends BaseEntity {

	public static final int SLEEP_MODE_INTERVAL_CONDITION = 5;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	private LocalTime wakeUpTime;

	private Integer dayOfTheWeeks;

	private Integer repeatFrequency;

	private Integer repeatInterval;

	private String wakeUpCallPath;

	private boolean isActive;

	protected Alarm() {
	}

	public Alarm(Long userId,
		LocalTime wakeUpTime,
		Integer dayOfTheWeeks,
		Integer repeatFrequency,
		Integer repeatInterval,
		boolean isActive,
		String wakeUpCallPath) {
		this.userId = userId;
		this.wakeUpTime = wakeUpTime;
		this.dayOfTheWeeks = dayOfTheWeeks;
		this.repeatFrequency = repeatFrequency;
		this.repeatInterval = repeatInterval;
		this.isActive = isActive;
		this.wakeUpCallPath = wakeUpCallPath;
	}

	public static Alarm initialize(Long userId) {
		return new Alarm(
			userId,
			LocalTime.of(7, 0),
			0,
			0,
			0,
			false,
			""
		);
	}

	public Long getId() {
		return id;
	}

	public Long getUserId() {
		return userId;
	}

	public LocalTime getWakeUpTime() {
		return wakeUpTime;
	}

	public int getDayOfTheWeeks() {
		return dayOfTheWeeks;
	}

	public Integer getRepeatFrequency() {
		return repeatFrequency;
	}

	public Integer getRepeatInterval() {
		return repeatInterval;
	}

	public boolean isActive() {
		return isActive;
	}

	public String getWakeUpCallPath() {
		return wakeUpCallPath;
	}

	public void set(LocalTime alarmTime, Integer weekDays, Integer repeatFrequency, Integer repeatInterval,
		boolean isActive) {
		this.wakeUpTime = alarmTime;
		this.dayOfTheWeeks = weekDays;
		this.repeatFrequency = repeatFrequency;
		this.repeatInterval = repeatInterval;
		this.isActive = isActive;
	}

	public void addMusicFilePath(String musicFilePath) {
		this.wakeUpCallPath = musicFilePath;
	}

	public boolean canSleepMode(LocalDateTime now) {
		DayOfTheWeek toDayOfTheWeek = DayOfTheWeek.getDayOfWeek(
			now.plusHours(SLEEP_MODE_INTERVAL_CONDITION)
				.toLocalDate().getDayOfWeek().name());

		boolean is5HoursBeforeTheAlarmStarts = this.wakeUpTime
			.minusHours(SLEEP_MODE_INTERVAL_CONDITION)
			.isAfter(now.toLocalTime().minusMinutes(1));
		boolean isTodayTheAlarmStartDate = (this.dayOfTheWeeks & toDayOfTheWeek.getShiftedBit()) > 0;

		return is5HoursBeforeTheAlarmStarts && isTodayTheAlarmStartDate;
	}
}
