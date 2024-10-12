package com.sf.honeymorning.domain.brief.dto.response.detail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class WordDto {
	private String word;
	private double weight;
}
