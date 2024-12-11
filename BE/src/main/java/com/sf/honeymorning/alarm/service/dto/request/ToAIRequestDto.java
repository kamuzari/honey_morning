package com.sf.honeymorning.alarm.service.dto.request;

import java.util.List;

public record ToAIRequestDto(
	Long userId,
	List<String> tags
) {
}
