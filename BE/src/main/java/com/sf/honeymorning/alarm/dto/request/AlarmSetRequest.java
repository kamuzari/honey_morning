package com.sf.honeymorning.alarm.dto.request;

import java.time.LocalTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AlarmSetRequest(
	@NotNull
	Long alarmId,

	@NotNull
	LocalTime alarmTime,

	@Min(1)
	@Max(127)
	@NotNull
	Integer weekdays,

	@Positive
	@Min(1)
	@Max(10)
	@NotNull
	Integer repeatFrequency,

	@Positive
	@Min(1)
	@Max(10)
	@NotNull
	Integer repeatInterval,

	@NotNull
	Boolean isActive
) {

}
