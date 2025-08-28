package com.honeymorning.api.alarm.application.service.dto.response;

import java.util.List;

public record AiQuizDto(
	String problem,
	Integer answer,
	List<String> selections
) {
}
