package com.honeymorning.api.brief.adapter.in.web.dto.response.detail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class WordResponseDto {
	private String word;
	private double weight;
}
