package com.sf.honeymorning.alarm.entity;

import java.time.LocalTime;

import com.sf.honeymorning.domain.common.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "alarms")
@Entity
public class Alarm extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	private LocalTime alarmTime;

	/**
	 * 한주의 알람을 비트마스킹으로 표현합니다. (총 7비트로 월요일부터 시작)
	 * 예시)
	 * - 0000001 = Sunday only
	 * - 1000001 = Monday and Sunday
	 * - 0110010 = TuesDay and Thursday and Saturday
	 * - 1111111 = Every day
	 */
	private String daysOfWeek;

	private Integer repeatFrequency = 0;

	private Integer repeatInterval;

	private boolean isActive;

	private String musicFilePath;

	public Alarm(Long userId, LocalTime alarmTime, String daysOfWeek, Integer repeatFrequency,
		Integer repeatInterval, boolean isActive, String musicFilePath) {
		this.userId = userId;
		this.alarmTime = alarmTime;
		this.daysOfWeek = daysOfWeek;
		this.repeatFrequency = repeatFrequency;
		this.repeatInterval = repeatInterval;
		this.isActive = isActive;
		this.musicFilePath = musicFilePath;
	}

	public static Alarm initialize(Long userId) {
		return new Alarm(
			userId,
			LocalTime.of(7, 0),
			"0000000",
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

	public LocalTime getAlarmTime() {
		return alarmTime;
	}

	public String getDaysOfWeek() {
		return daysOfWeek;
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

	public String getMusicFilePath() {
		return musicFilePath;
	}

	public void set(LocalTime alarmTime, String daysOfWeek, Integer repeatFrequency, Integer repeatInterval,
		boolean isActive) {
		this.alarmTime = alarmTime;
		this.daysOfWeek = daysOfWeek;
		this.repeatFrequency = repeatFrequency;
		this.repeatInterval = repeatInterval;
		this.isActive = isActive;
	}
}
