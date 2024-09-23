package com.sf.honeymorning.domain.brief.dto.response.detail;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SummaryResponseDto {
	@Schema(name = "워드 클라우드's")
	private List<WordCloudResponseDto> wordCloudResponses;

	@Schema(name = "카테고리's", example = "['경제', '정치', '고냥이']")
	private List<String> categories;
}
