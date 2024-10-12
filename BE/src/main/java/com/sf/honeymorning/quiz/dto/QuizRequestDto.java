package com.sf.honeymorning.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class QuizRequestDto {
	private Long id;
	private Long briefId;
	private String question;
	private Integer answer;
	private String option1;
	private String option2;
	private String option3;
	private String option4;
	private Integer selection;
}
