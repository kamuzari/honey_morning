package com.honeymorning.relay.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.honeymorning.relay.config.constant.VoiceClientProperties;

@Configuration
@EnableConfigurationProperties({VoiceClientProperties.class})
public class ExternalApiConfig {
	private final VoiceClientProperties voiceClientProperties;

	public ExternalApiConfig(VoiceClientProperties voiceClientProperties) {
		this.voiceClientProperties = voiceClientProperties;
	}
}
