package com.honeymorning.relay.config.external;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({VoiceClientProperties.class})
public class ExternalApiConfig {
	private final VoiceClientProperties voiceClientProperties;

	public ExternalApiConfig(VoiceClientProperties voiceClientProperties) {
		this.voiceClientProperties = voiceClientProperties;
	}
}
