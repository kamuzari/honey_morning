package com.honeymorning.api.brief.adapter.in.web.dto.response;

import java.util.List;

import com.honeymorning.api.brief.adapter.in.web.dto.response.briefs.MyBriefing;

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
	@ArraySchema(schema = @Schema(implementation = MyBriefing.class))
	List<MyBriefing> myBriefings;

	@Schema(example = "숫자 - 총 페이지 개수 ")
	int totalPage;
}
