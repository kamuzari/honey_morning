package com.honeymorning.api.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.honeymorning.api.config.constant.VoiceClientProperties;

@Configuration
@EnableConfigurationProperties({VoiceClientProperties.class})
public class ExternalApiConfig {
	private final VoiceClientProperties voiceClientProperties;

	public ExternalApiConfig(VoiceClientProperties voiceClientProperties) {
		this.voiceClientProperties = voiceClientProperties;
	}
}
