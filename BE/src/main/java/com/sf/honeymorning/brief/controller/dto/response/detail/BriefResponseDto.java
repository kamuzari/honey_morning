package com.sf.honeymorning.brief.controller.dto.response.detail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class BriefResponseDto {
	@Schema(example = "브리핑 요약 - 오늘의 날씨는 ... ... 입니다.")
	private String summary;

	@Schema(example = "본문 - 이번 증시 차트표가 고냥이 상과 유사하게 그려져 앞으로 ... ... 입니다.")
	private String content;

	@Schema(example = "본문 음성 출력 파일")
	private String contentFilePath;

}
