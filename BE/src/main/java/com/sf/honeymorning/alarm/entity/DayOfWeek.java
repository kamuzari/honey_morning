package com.sf.honeymorning.alarm.entity;

import java.time.LocalDate;

public enum DayOfWeek {
	MONDAY(1, "월요일"),
	TUESDAY(1 << 1, "화요일"),
	WEDNESDAY(1 << 2, "수요일"),
	THURSDAY(1 << 3, "목요일"),
	FRIDAY(1 << 4, "금요일"),
	SATURDAY(1 << 5, "토요일"),
	SUNDAY(1 << 6, "일요일");

	private final int bit;
	private final String dayName;

	DayOfWeek(int bit, String dayName) {
		this.bit = bit;
		this.dayName = dayName;
	}

	public int getBit() {
		return bit;
	}

	public static byte getToday() {
		return (byte)(LocalDate.now().getDayOfWeek().getValue() - 1);
	}

	public static byte toBit(DayOfWeek... days) {
		return (byte)java.util.Arrays.stream(days)
			.mapToInt(DayOfWeek::getBit)
			.reduce(0, (a, b) -> a | b);
	}
}
