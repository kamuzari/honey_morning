package com.sf.honeymorning.brief.controller.dto.response.detail;

import io.swagger.v3.oas.annotations.media.Schema;

public record QuizResponseDto(
	@Schema(example = "문제 - 돈을 빌리고 빌려주는 과정에서 이루어지는 돈의 흐름은?")
	String question,

	@Schema(example = "보기1- 금전")
	String option1,

	@Schema(example = "보기2 - 금리")
	String option2,

	@Schema(example = "보기3 - 금융")
	String option3,

	@Schema(example = "보기4 - 투자")
	String option4,

	@Schema(example = "1")
	Integer selectedOption,

	@Schema(example = "0")
	Integer answerNumber
) {

}


