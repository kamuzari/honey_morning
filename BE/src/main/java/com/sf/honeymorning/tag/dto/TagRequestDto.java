package com.sf.honeymorning.tag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TagRequestDto {
	private String word;
}
