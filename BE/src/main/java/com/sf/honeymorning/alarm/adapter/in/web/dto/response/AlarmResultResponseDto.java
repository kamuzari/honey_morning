package com.sf.honeymorning.alarm.adapter.in.web.dto.response;

import java.time.LocalDateTime;

public record AlarmResultResponseDto(
	Long id,
	Integer count,
	boolean isAttending,
	LocalDateTime createdAt
) {
}
