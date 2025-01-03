package com.sf.honeymorning.alarm.domain.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.StringJoiner;

import com.sf.honeymorning.common.entity.basic.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
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

	@Column(name = "wake_up")
	private String wakeUpCallPath;

	private boolean isActive;

	protected Alarm() {
	}

	public Alarm(
		Long id,
		Long userId,
		LocalTime wakeUpTime,
		Integer dayOfTheWeeks,
		Integer repeatFrequency,
		Integer repeatInterval,
		boolean isActive,
		String wakeUpCallPath) {
		this.id = id;
		this.userId = userId;
		this.wakeUpTime = wakeUpTime;
		this.dayOfTheWeeks = dayOfTheWeeks;
		this.repeatFrequency = repeatFrequency;
		this.repeatInterval = repeatInterval;
		this.isActive = isActive;
		this.wakeUpCallPath = wakeUpCallPath;
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

	public void set(LocalTime alarmTime, Integer weekDays, Integer repeatFrequency, Integer repeatInterval,
		boolean isActive) {
		this.wakeUpTime = alarmTime;
		this.dayOfTheWeeks = weekDays;
		this.repeatFrequency = repeatFrequency;
		this.repeatInterval = repeatInterval;
		this.isActive = isActive;
	}

	public void addContent(String wakeUpCallPath) {
		this.wakeUpCallPath = wakeUpCallPath;
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

	@Override
	public String toString() {
		return new StringJoiner(", ", Alarm.class.getSimpleName() + "[", "]")
			.add("id=" + id)
			.add("userId=" + userId)
			.add("wakeUpTime=" + wakeUpTime)
			.add("dayOfTheWeeks=" + dayOfTheWeeks)
			.add("repeatFrequency=" + repeatFrequency)
			.add("repeatInterval=" + repeatInterval)
			.add("wakeUpCallPath='" + wakeUpCallPath + "'")
			.add("isActive=" + isActive)
			.toString();
	}
}
