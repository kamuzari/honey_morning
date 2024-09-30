package com.sf.honeymorning.domain.alarm.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AlarmStartDto {
	@Schema(example = "AI 모닝콜 경로")
	private String morningCallUrl;

	@ArraySchema(schema = @Schema(implementation = QuizDto.class))
	private List<QuizDto> quizzes;

	@Schema(example = "브리핑 내용")
	private String briefingContent;

	@Schema(example = "브리핑 음성 경로")
	private String briefingContentUrl;
}
