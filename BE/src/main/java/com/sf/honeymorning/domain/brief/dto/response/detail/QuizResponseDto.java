package com.sf.honeymorning.domain.brief.dto.response.detail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class QuizResponseDto {
	@Schema(name = "문제", example = "돈을 빌리고 빌려주는 과정에서 이루어지는 돈의 흐름은?")
	private String question;

	@Schema(name = "보기1", example = "금전")
	private String option1;

	@Schema(name = "보기2", example = "금리")
	private String option2;

	@Schema(name = "보기3", example = "금융")
	private String option3;

	@Schema(name = "보기4", example = "투자")
	private String option4;

	@Schema(name = "내가 선택한 보기 숫자", example = "1")
	private Integer selectedOption;

	@Schema(name = "정답 번호", example = "1")
	private Integer answerNumber;
}


