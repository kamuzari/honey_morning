package com.honeymorning.batch.alarm.application.dto;

import java.util.List;

public record ToAiRequestDto(
	Long userId,
	List<String> tags
) {
}
