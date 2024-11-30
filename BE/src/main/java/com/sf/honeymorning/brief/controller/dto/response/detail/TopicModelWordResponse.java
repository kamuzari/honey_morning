package com.sf.honeymorning.brief.controller.dto.response.detail;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TopicModelWordResponse {
	private Long id;
	private List<WordResponseDto> words;
}
