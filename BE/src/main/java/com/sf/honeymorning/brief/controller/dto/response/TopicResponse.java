package com.sf.honeymorning.brief.controller.dto.response;

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