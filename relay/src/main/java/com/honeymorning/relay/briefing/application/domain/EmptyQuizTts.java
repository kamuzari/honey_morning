package com.honeymorning.relay.briefing.application.domain;

import com.honeymorning.common.common.content.Content;

import lombok.Getter;

@Getter
public class EmptyQuizTts {
	private final Long quizId;
	private Content tts;

	public EmptyQuizTts(Long quizId, Content Tts) {
		if (Tts != null) {
			throw new IllegalStateException("최신 브리핑은 TTS 컨텐츠가 없어야 합니다.");
		}

		this.quizId = quizId;
		this.tts = null;
	}

	public void add(Content content) {
		this.tts = content;
	}
}
