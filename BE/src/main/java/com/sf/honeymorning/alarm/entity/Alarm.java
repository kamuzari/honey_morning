package com.sf.honeymorning.alarm.entity;

import java.time.LocalTime;
import java.util.StringJoiner;

import com.sf.honeymorning.common.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "alarms")
@Entity
public class Alarm extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	private LocalTime wakeUpTime;

	/**
	 * 알람을 비트마스킹으로 표현합니다.
	 * 맨 오른쪽 부터 월요일입니다.
	 * 맨 왼쪽 비트는 사용하지 않아요.
	 * 왜냐하면 2의 보수의 원리로 인한 이슈가 있기 때문이에요 :)
	 * 예시)
	 * - 0100 0000 = Sunday only
	 * - 0100 0001 = Monday and Sunday
	 * - 0011 0010 = TuesDay and Thursday and Saturday
	 * - 0111 1111 = Every day
	 */
	private byte dayOfWeek;

	private Integer repeatFrequency;

	private Integer repeatInterval;

	private boolean isActive;

	private String musicFilePath;

	protected Alarm() {
	}

	public Alarm(Long userId,
		LocalTime wakeUpTime,
		byte dayOfWeek,
		Integer repeatFrequency,
		Integer repeatInterval,
		boolean isActive,
		String musicFilePath) {
		this.userId = userId;
		this.wakeUpTime = wakeUpTime;
		this.dayOfWeek = dayOfWeek;
		this.repeatFrequency = repeatFrequency;
		this.repeatInterval = repeatInterval;
		this.isActive = isActive;
		this.musicFilePath = musicFilePath;
	}

	public static Alarm initialize(Long userId) {
		return new Alarm(
			userId,
			LocalTime.of(7, 0),
			(byte)0,
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

	public byte getDayOfWeek() {
		return dayOfWeek;
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

	public void set(LocalTime alarmTime, byte weekDays, Integer repeatFrequency, Integer repeatInterval,
		boolean isActive) {
		this.wakeUpTime = alarmTime;
		this.dayOfWeek = weekDays;
		this.repeatFrequency = repeatFrequency;
		this.repeatInterval = repeatInterval;
		this.isActive = isActive;
	}

	public void addMusicFilePath(String musicFilePath) {
		this.musicFilePath = musicFilePath;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", Alarm.class.getSimpleName() + "[", "]")
			.add("id=" + id)
			.add("userId=" + userId)
			.add("wakeUpTime=" + wakeUpTime)
			.add("dayOfWeek=" + dayOfWeek)
			.add("repeatFrequency=" + repeatFrequency)
			.add("repeatInterval=" + repeatInterval)
			.add("isActive=" + isActive)
			.add("musicFilePath='" + musicFilePath + "'")
			.toString();
	}
}
