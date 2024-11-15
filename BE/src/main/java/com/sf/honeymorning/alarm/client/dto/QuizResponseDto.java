package com.sf.honeymorning.alarm.client.dto;

import java.util.List;

public record QuizResponseDto(
	String problem,
	List<QuizOption> quizOptions,
	Integer answer
) {
}
