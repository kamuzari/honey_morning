package com.honeymorning.api.alarm.adapter.out.external.dto;

import java.util.List;

public record QuizResponseDto(
	String problem,
	List<QuizOption> quizOptions,
	Integer answer
) {
}
