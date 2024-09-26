package com.sf.honeymorning.domain.alarm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class QuizDto {
	@Schema(name = "퀴즈 id")
	private Long id;
	@Schema(name = "문제")
	private String question;
	@Schema(name = "정답")
	private Integer answer;
	@Schema(name = "선택지 1")
	private String option1;
	@Schema(name = "선택지 2")
	private String option2;
	@Schema(name = "선택지 3")
	private String option3;
	@Schema(name = "선택지 4")
	private String option4;
}
