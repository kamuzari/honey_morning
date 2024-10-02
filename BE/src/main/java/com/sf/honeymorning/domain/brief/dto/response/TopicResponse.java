package com.sf.honeymorning.domain.brief.dto.response;

import java.util.List;

public class TopicResponse {

	private List<TopicAiResponseDto> data;

	// Getters and Setters

	public List<TopicAiResponseDto> getData() {
		return data;
	}

	public void setData(List<TopicAiResponseDto> data) {
		this.data = data;
	}
}