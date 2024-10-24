package com.sf.honeymorning.alarm.dto.response;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "나의 알람 설정 가져오기 응답 객체")
public record AlarmResponse(
	@Schema(description = "식별자", example = "1")
	Long id,

	@Schema(description = "사용자가 설정한 알람 시각", example = "07:00")
	LocalTime wakeUpTime,

	@Schema(description = "맨 오른쪽 부터 1이 활성화된 요일이에요", example = "1111111")
	byte daysOfWeek,

	@Schema(description = "반복 횟수", example = "3")
	Integer repeatFrequency,

	@Schema(description = "반복 간격", example = "5")
	Integer repeatInterval,

	@Schema(description = "활성화 여부", example = "1")
	boolean isActive
) {
}
