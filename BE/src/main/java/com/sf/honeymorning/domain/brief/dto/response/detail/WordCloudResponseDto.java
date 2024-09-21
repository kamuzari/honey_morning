package com.sf.honeymorning.domain.brief.dto.response.detail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class WordCloudResponseDto {
	@Schema(name = "키워드", example = "고냥이")
	private String word;
	@Schema(name = "출현 빈도수", example = "20303")
	private Integer frequency;
}
