package com.honeymorning.relay.briefing.application.service.dto;

import java.util.List;

public record AiQuizDto(
	Integer order,
	String problem,
	Integer answer,
	List<String> selections
) {
}
