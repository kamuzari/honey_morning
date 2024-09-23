package com.sf.honeymorning.domain.brief.dto.response;

import java.util.List;

import com.sf.honeymorning.domain.brief.dto.response.briefs.BriefHistoryDto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(name = "브리핑 기록 응답", description = "브리핑 기록 조회에서 필요한 응답 모델이에요 📦")
public class BriefHistoryResponseDto {
	@ArraySchema(schema = @Schema(implementation = BriefHistoryDto.class))
	List<BriefHistoryDto> histories;

	@Schema(description = "총 페이지 개수", example = "보여주는 ")
	int totalPage;
}
