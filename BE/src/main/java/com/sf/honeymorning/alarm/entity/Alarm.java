package com.sf.honeymorning.alarm.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.StringJoiner;

import com.sf.honeymorning.common.entity.BaseEntity;
import com.sf.honeymorning.util.TimeUtils;

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

	/**
	 * - 알람을 비트마스킹으로 표현합니다.
	 * - 맨 오른쪽 부터 월요일입니다.
	 * - 맨 왼쪽 비트는 사용하지 않아요.
	 * - 왜냐하면 2의 보수의 원리로 인한 이슈가 있기 때문이에요 :)
	 * 예시)
	 *   - 0100 0000 = Sunday only
	 *   - 0100 0001 = Monday and Sunday
	 *   - 0011 0010 = TuesDay and Thursday and Saturday
	 *   - 0111 1111 = Every day
	 */
	private Integer dayOfWeek;

	private Integer repeatFrequency;

	private Integer repeatInterval;

	private String wakeUpCallPath;

	private boolean isActive;

	protected Alarm() {
	}

	public Alarm(Long userId,
		LocalTime wakeUpTime,
		Integer dayOfWeek,
		Integer repeatFrequency,
		Integer repeatInterval,
		boolean isActive,
		String wakeUpCallPath) {
		this.userId = userId;
		this.wakeUpTime = wakeUpTime;
		this.dayOfWeek = dayOfWeek;
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

	public int getDayOfWeek() {
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

	public String getWakeUpCallPath() {
		return wakeUpCallPath;
	}

	public void set(LocalTime alarmTime, Integer weekDays, Integer repeatFrequency, Integer repeatInterval,
		boolean isActive) {
		this.wakeUpTime = alarmTime;
		this.dayOfWeek = weekDays;
		this.repeatFrequency = repeatFrequency;
		this.repeatInterval = repeatInterval;
		this.isActive = isActive;
	}

	public void addMusicFilePath(String musicFilePath) {
		this.wakeUpCallPath = musicFilePath;
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
			.add("musicFilePath='" + wakeUpCallPath + "'")
			.toString();
	}

	public boolean canSleepMode(LocalDateTime now) {
		DayOfWeek today = DayOfWeek.getDayOfWeek(now.toLocalDate().getDayOfWeek().name());
		boolean is5HoursBeforeTheAlarmStarts = this.wakeUpTime
			.minusHours(SLEEP_MODE_INTERVAL_CONDITION)
			.isAfter(now.toLocalTime().plusMinutes(1));
		boolean isTodayTheAlarmStartDate = (this.dayOfWeek & today.getBit()) > 0;

		return is5HoursBeforeTheAlarmStarts && isTodayTheAlarmStartDate;
	}

	public static void main(String[] args) {
		LocalTime wakeUpTime = TimeUtils.getNow();
		System.out.println("wakeUpTime = " + wakeUpTime);
		LocalTime time5HourAgo = wakeUpTime.minusHours(5).plusMinutes(0);
		System.out.println("time5HourAgo = " + time5HourAgo);

		System.out.println(time5HourAgo.isAfter(TimeUtils.getNow().minusHours(6))); // false
		// 10:42 분 알람시간인데 현재 5:42분이면 수면모드 가능 5:43분 부터는 모두 불가능

		Arrays.stream(DayOfWeek.values()).forEach(v -> System.out.println(v.getBit()));
		System.out.println();
		Integer 월토 = DayOfWeek.toBit(DayOfWeek.MONDAY, DayOfWeek.SATURDAY);
		System.out.println("월토 = " + 월토);
		Alarm alarm = new Alarm(1L, TimeUtils.getNow(),
			월토,
			1, 1, true, "empty");
		int dayOfWeek = alarm.getDayOfWeek();

		// Integer today = DayOfWeek.FRIDAY.getBit();
		// Integer today = DayOfWeek.getToday();
		Integer today = DayOfWeek.SUNDAY.getBit();
		System.out.println("today = " + today);
		// 0111 1111 -> every day , 0010 0001 -> 월,토
		boolean b = (dayOfWeek & today) > 0;
		System.out.println(b);

		System.out.println("==========");
		LocalDateTime now = LocalDateTime.now();
		java.time.DayOfWeek dayOfWeek1 = now.toLocalDate().getDayOfWeek();
		System.out.println(dayOfWeek1);
	}

}
