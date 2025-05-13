package com.sf.honeymorning.alarm.adapter.out.persistence.entity;

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
public class AlarmEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	private LocalTime wakeUpTime;

	private Integer dayOfTheWeeks;

	private Integer repeatFrequency;

	private Integer repeatInterval;

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
