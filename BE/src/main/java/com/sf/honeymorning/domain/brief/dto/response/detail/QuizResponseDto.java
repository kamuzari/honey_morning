package com.sf.honeymorning.domain.brief.dto.response.detail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class QuizResponseDto {
	@Schema(example = "문제 - 돈을 빌리고 빌려주는 과정에서 이루어지는 돈의 흐름은?")
	private String question;

	@Schema(example = "보기1- 금전")
	private String option1;

	@Schema(example = "보기2 - 금리")
	private String option2;

	@Schema(example = "보기3 - 금융")
	private String option3;

	@Schema(example = "보기4 - 투자")
	private String option4;

	@Schema(example = "1")
	private Integer selectedOption;

	@Schema(example = "0")
	private Integer answerNumber;
}


