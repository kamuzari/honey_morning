package com.sf.honeymorning.config.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "voice.client.resources")
public record VoiceClientProperties(
	Path path,
	Header header,
	Body body
) {

	public record Path(
		String voiceId
	) {

	}

	public record Header(
		int optimizeStreamingLatency,
		String outputFormat,
		String xiApiKey
	) {

	}

	public record Body(
		String modelId,
		double stability,
		double similarityBoost,
		double style
	) {

	}
}
