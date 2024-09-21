package com.sf.honeymorning.domain.brief.dto.response;

import java.util.List;

import com.sf.honeymorning.domain.brief.dto.response.detail.BriefResponseDto;
import com.sf.honeymorning.domain.brief.dto.response.detail.QuizResponseDto;
import com.sf.honeymorning.domain.brief.dto.response.detail.SummaryResponseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class BriefDetailResponseDto {
	@Schema(description = "브리핑 ID", example = "1")
	private Long briefId;

	@Schema(name = "[TAP] 요약")
	private SummaryResponseDto summaryDto;

	@Schema(name = "[TAP] 브리핑")
	private BriefResponseDto briefDto;

	@Schema(name = "[TAP] 퀴즈 목록 (최대 2문제)")
	private List<QuizResponseDto> quizDto;
}
