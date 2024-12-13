package com.sf.honeymorning.alarm.entity;

import java.time.LocalDate;
import java.util.Arrays;

import com.sf.honeymorning.common.exception.model.ErrorProtocol;
import com.sf.honeymorning.common.exception.model.UnExpectedFatalException;

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

	public static Integer getToday() {
		return LocalDate.now().getDayOfWeek().getValue();
	}

	public static Integer toBit(DayOfWeek... days) {
		return Arrays.stream(days)
			.mapToInt(DayOfWeek::getBit)
			.reduce(0, (a, b) -> a | b);
	}

	public static DayOfWeek getDayOfWeek(String dayName) {
		return Arrays.stream(DayOfWeek.values())
			.filter(dayOfWeek -> dayOfWeek.name().equalsIgnoreCase(dayName))
			.findAny()
			.orElseThrow(() -> new UnExpectedFatalException("날짜를 불러올 수 없습니다.", ErrorProtocol.UNEXPECTED_FATAL_ERROR));
	}

	public Integer getBit() {
		return bit;
	}

}
