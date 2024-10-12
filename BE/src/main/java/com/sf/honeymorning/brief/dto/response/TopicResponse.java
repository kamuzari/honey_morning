package com.sf.honeymorning.domain.brief.dto.response;

import java.util.List;

public class TopicResponse {

	private List<com.sf.honeymorning.domain.brief.dto.response.TopicAiResponseDto> data;

	// Getters and Setters

	public List<com.sf.honeymorning.domain.brief.dto.response.TopicAiResponseDto> getData() {
		return data;
	}

	public void setData(List<com.sf.honeymorning.domain.brief.dto.response.TopicAiResponseDto> data) {
		this.data = data;
	}
}