package com.sf.honeymorning.alarm.dto.response;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알람 응답 DTO")
public record AlarmResponse(
	@Schema(description = "아이디", example = "1")
	Long id,

	@Schema(description = "알람 시각", example = "07:00:00")
	LocalTime alarmTime,

	@Schema(description = "요일", example = "1111111")
	byte daysOfWeek,

	@Schema(description = "반복 횟수", example = "3")
	Integer repeatFrequency,

	@Schema(description = "반복 간격", example = "5")
	Integer repeatInterval,

	@Schema(description = "활성화 여부", example = "1")
	boolean isActive
) {
}
