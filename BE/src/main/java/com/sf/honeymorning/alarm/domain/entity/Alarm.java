package com.sf.honeymorning.alarm.domain.entity;

import static com.sf.honeymorning.alarm.common.AlarmConstraint.SLEEP_MODE_INTERVAL_CONDITION;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.sf.honeymorning.common.entity.basic.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Table(name = "alarms")
@Entity
public class Alarm extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	private LocalTime wakeUpTime;

	private Integer dayOfTheWeeks;

	private Integer repeatFrequency;

	private Integer repeatInterval;

	private boolean isActive;

	protected Alarm() {
	}

	public Alarm(Long userId,
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

	public static Alarm initialize(Long userId) {
		return new Alarm(
			userId,
			LocalTime.of(7, 0),
			0,
			0,
			0,
			false
		);
	}

	public void update(LocalTime alarmTime,
		Integer dayOfTheWeeks,
		Integer repeatFrequency,
		Integer repeatInterval,
		boolean isActive) {
		this.wakeUpTime = alarmTime;
		this.dayOfTheWeeks = dayOfTheWeeks;
		this.repeatFrequency = repeatFrequency;
		this.repeatInterval = repeatInterval;
		this.isActive = isActive;
	}

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
