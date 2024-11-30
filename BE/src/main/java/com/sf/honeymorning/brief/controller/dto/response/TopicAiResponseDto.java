package com.sf.honeymorning.brief.controller.dto.response;

import java.util.List;

public class TopicAiResponseDto {
	private int topic_id;
	private List<TopicWord> topic_words;

	// Getters and Setters

	public int getTopic_id() {
		return topic_id;
	}

	public void setTopic_id(int topic_id) {
		this.topic_id = topic_id;
	}

	public List<TopicWord> getTopic_words() {
		return topic_words;
	}

	public void setTopic_words(List<TopicWord> topic_words) {
		this.topic_words = topic_words;
	}
}
