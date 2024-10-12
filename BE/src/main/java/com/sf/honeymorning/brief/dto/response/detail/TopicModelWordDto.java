package com.sf.honeymorning.domain.brief.dto.response.detail;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TopicModelWordDto {
	private Long topic_id;
	private List<com.sf.honeymorning.domain.brief.dto.response.detail.WordDto> topic_words;
}
