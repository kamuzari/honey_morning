package com.honeymorning.relay.briefing.application.domain;

import com.honeymorning.common.common.content.Content;

import lombok.Getter;

@Getter
public class EmptyBriefingTts {
	private final Long briefingId;
	private Content tts;

	public EmptyBriefingTts(Long briefingId, Content tts) {
		if (tts != null) {
			throw new IllegalStateException("최신 브리핑은 TTS 컨텐츠가 없어야 합니다.");
		}

		this.briefingId = briefingId;
		this.tts = null;
	}

	public void add(Content content) {
		this.tts = content;
	}
}
