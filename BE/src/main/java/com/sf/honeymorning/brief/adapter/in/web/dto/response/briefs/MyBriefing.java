package com.sf.honeymorning.brief.adapter.in.web.dto.response.briefs;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "브리핑 기록", description = "브리핑 목록 조회에서 필요한 목록들이에요 📦")
public record MyBriefing(
	Long briefId,

	LocalDateTime createdAt,

	List<String> categories,

	@Schema(example = "브리핑 요약 - 오늘의 날씨는 ... ... 입니다.")
	String summary,

	@Schema(example = " 숫자 - 총 2문제 중 맞춘 정답수 [0,1,2] 중 1개")
	Long numberOfCorrectAnswer
) {

}
