package com.sf.honeymorning.brief.adapter.out.external.dto.request;

import com.sf.honeymorning.config.constant.VoiceClientProperties;

import lombok.Getter;

@Getter
public class VoiceCreateRequestDto {
	String text;
	String modelId;
	VoiceSettings voiceSettings;

	protected VoiceCreateRequestDto() {
	}

	public VoiceCreateRequestDto(String text, VoiceClientProperties voiceClientProperties) {
		this.text = text;
		this.modelId = voiceClientProperties.body().modelId();
		this.voiceSettings = new VoiceSettings(
			voiceClientProperties.body().stability(),
			voiceClientProperties.body().similarityBoost(),
			voiceClientProperties.body().style()
		);
	}

	public record VoiceSettings(
		double stability,
		double similarityBoost,
		double style
	) {
	}
}
