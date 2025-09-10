package com.honeymorning.api.alarm.adapter.out.persistence.entity;

import java.time.LocalTime;

import com.honeymorning.common.common.basic.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Table(name = "alarms",
	indexes = {
		@Index(name = "alarm_user_id_idx", columnList = "user_id")
	})
@Entity
public class AlarmEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "wake_up_time", nullable = false)
	private LocalTime wakeUpTime;

	@Column(name = "day_of_the_weeks", nullable = false)
	private Integer dayOfTheWeeks;

	@Column(name = "repeat_frequency", nullable = false)
	private Integer repeatFrequency;

	@Column(name = "repeat_interval", nullable = false)
	private Integer repeatInterval;

	@Column(name = "is_active", nullable = false)
	private boolean isActive;

	protected AlarmEntity() {
	}

	public AlarmEntity(
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

	public static AlarmEntity initialize(Long userId) {
		return new AlarmEntity(
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
		boolean isActive) {

		this.wakeUpTime = alarmTime;
		this.dayOfTheWeeks = dayOfTheWeeks;
		this.repeatFrequency = repeatFrequency;
		this.repeatInterval = repeatInterval;
		this.isActive = isActive;
	}

}
