package com.sf.honeymorning.domain.brief.dto.response.briefs;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(name = "브리핑 기록", description = "브리핑 목록 조회에서 필요한 목록들이에요 📦")
public class BriefHistoryDto {
	@Schema(example = "숫자 - 브리핑 ID")
	private Long briefId;

	@Schema(example = "생성일자 - 2023-09-10T12:30:00")
	private LocalDateTime createdAt;

	@Schema(example = "카테고리 리스트 - ['경제', '정치', '고냥이']")
	private List<String> categories;

	@Schema(example = "브리핑 요약 - 오늘의 날씨는 ... ... 입니다.")
	private String summary;

	@Schema(example = " 숫자 - 총 2문제 중 맞춘 정답수 [0,1,2] 중 1개")
	private Long numberOfCorrectAnswer;
}
