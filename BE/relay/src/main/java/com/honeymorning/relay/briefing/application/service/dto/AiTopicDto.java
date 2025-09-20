package com.honeymorning.relay.briefing.application.service.dto;

public record AiTopicDto(
	int sectionId,
	String word,
	Double weight
) {
}
