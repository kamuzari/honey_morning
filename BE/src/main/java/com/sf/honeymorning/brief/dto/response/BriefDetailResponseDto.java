package com.sf.honeymorning.domain.brief.dto.response;

import java.time.LocalDateTime;
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
@Schema(name = "브리핑 상세 목록 응답", description = "브리핑 상세 조회에서 필요한 응답 모델이에요 📦")
@Data
public class BriefDetailResponseDto {
	@Schema(example = "숫자 - 브리핑 ID")
	private Long briefId;

	private SummaryResponseDto summaryDto;

	private BriefResponseDto briefDto;

	private List<QuizResponseDto> quizDto;

	@Schema(example = "title 앞에 쓰일 생성일자")
	private LocalDateTime createdAt;
}
