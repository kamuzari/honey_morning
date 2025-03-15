package com.sf.honeymorning.alarm.controller.dto.response;

import java.time.LocalDateTime;

public record AlarmResultResponseDto(
	Long id,
	Integer count,
	boolean isAttending,
	LocalDateTime createdAt
) {
}
