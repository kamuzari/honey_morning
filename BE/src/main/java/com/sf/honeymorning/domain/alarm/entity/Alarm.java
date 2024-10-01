package com.sf.honeymorning.domain.alarm.entity;

import java.time.LocalTime;
import java.util.StringJoiner;

import com.sf.honeymorning.domain.common.entity.BaseEntity;
import com.sf.honeymorning.domain.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@AllArgsConstructor
public class Alarm extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "alarm_id")
	private Long id;

	@JoinColumn(name = "user_id")
	@OneToOne(cascade = CascadeType.REMOVE)
	private User user;

	/**
	 * 알람 시간이 저장되는 컬럼입니다.
	 * 예시)
	 * LocalTime.of(14, 23) = 14시 23분
	 */
	@Column(nullable = false)
	private LocalTime alarmTime;

	/**
	 * 한주의 알람을 비트마스킹으로 표현합니다. (총 7비트로 월요일부터 시작)
	 * 예시)
	 * - 0000001 = Sunday only
	 * - 1000001 = Monday and Sunday
	 * - 0110010 = TuesDay and Thursday and Saturday
	 * - 1111111 = Every day
	 */
	@Column(nullable = false)
	private String daysOfWeek;

	@Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
	private Integer repeatFrequency = 0;

	@Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
	private Integer repeatInterval = 0;

	/**
	 * 알람 활성 여부를 0과 1로 표시합니다.
	 * 예시)
	 * - 0 = 비활성
	 * - 1 = 활성
	 */
	@Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
	private Integer isActive = 0;

	@Column(length = 1000, nullable = true)
	private String musicFilePath;

	@Override
	public String toString() {
		return new StringJoiner(", ", Alarm.class.getSimpleName() + "[", "]")
			.add("id=" + id)
			.add("user=" + user)
			.add("alarmTime=" + alarmTime)
			.add("daysOfWeek='" + daysOfWeek + "'")
			.add("repeatFrequency=" + repeatFrequency)
			.add("repeatInterval=" + repeatInterval)
			.add("isActive=" + isActive)
			.add("musicFilePath='" + musicFilePath + "'")
			.toString();
	}
}
