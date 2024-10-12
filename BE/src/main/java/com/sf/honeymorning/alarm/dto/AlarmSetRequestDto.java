package com.sf.honeymorning.alarm.dto;

import java.time.LocalTime;

public record AlarmSetRequestDto(
	Long id,
	LocalTime alarmTime,
	String daysOfWeek,
	Integer repeatFrequency,
	Integer repeatInterval,
	String musicFilePath,
	boolean isActive
) {
}
