package com.sf.honeymorning.alarm.service.dto.response;

public record AiTopicDto(
	int sectionId,
	String word,
	Double weight
) {
}
