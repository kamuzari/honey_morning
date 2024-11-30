package com.sf.honeymorning.brief.controller.dto.response.detail;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SummaryResponseDto {
	private List<TopicModelWordResponse> topicModelResponses;

	@Schema(example = "카테고리리스트 - ['경제', '정치', '고냥이']")
	private List<String> tagNames;
}
