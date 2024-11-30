package com.sf.honeymorning.brief.controller.dto.response.detail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class WordCloudResponseDto {
	@Schema(example = "키워드- 고냥이")
	private String word;
	@Schema(example = "20303")
	private Integer frequency;
}
