package com.sf.honeymorning.domain.brief.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "브리핑 기록", description = "브리핑 목록 조회에서 필요한 응답 모델이에요 📦")
public class BriefHistoryDto {
	@Schema(description = "브리프 ID", example = "1")
	private final Long briefId;

	@Schema(description = "생성 일자", example = "2023-09-10T12:30:00")
	private final LocalDateTime createdAt;

	@Schema(description = "카테고리 리스트", example = "['경제', '정치', '고냥이']")
	private final List<String> categories;

	@Schema(description = "브리핑 요약(음성 출력) ", example = "오늘의 날씨는 ... ... 입니다.")
	private final String summary;

	@Schema(description = "총 2문제 중 맞춘 정답수", example = "[0,1,2] 중 1개")
	private final Long numberOfCorrectAnswer;

	public BriefHistoryDto(Long briefId, LocalDateTime createdAt, List<String> categories, String summary,
		Long numberOfCorrectAnswer) {
		this.briefId = briefId;
		this.createdAt = createdAt;
		this.categories = categories;
		this.summary = summary;
		this.numberOfCorrectAnswer = numberOfCorrectAnswer;
	}
}
