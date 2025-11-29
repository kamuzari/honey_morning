package com.honeymorning.api.brief.adapter.in.web.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SelectionRequestDto(
	@NotNull
	Long briefingId,

	@NotNull
	@Size(min = 2, max = 2)
	List<SelectionQuizDto> selectionQuizDtos
) {

	public record SelectionQuizDto(
		Long quizId,
		Integer selection
	) {

	}
}
