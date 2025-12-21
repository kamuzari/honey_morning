package com.honeymorning.relay.infrastructure.external.elevenlabs;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.honeymorning.relay.briefing.application.port.out.CommandTextToSpeechPort;
import com.honeymorning.relay.config.external.VoiceClientProperties;

@Component
public class TextToSpeechClientAdapter implements CommandTextToSpeechPort {

	private final VoiceClient voiceClient;
	private final VoiceClientProperties voiceClientProperties;

	public TextToSpeechClientAdapter(
		VoiceClient voiceClient,
		VoiceClientProperties voiceClientProperties) {

		this.voiceClient = voiceClient;
		this.voiceClientProperties = voiceClientProperties;
	}

	@Retryable(maxAttempts = 2, backoff = @Backoff(delay = 1000))
	public ResponseEntity<Resource> create(String text) {
		return voiceClient.createTts(
			voiceClientProperties.path().voiceId(),
			voiceClientProperties.header().xiApiKey(),
			voiceClientProperties.header().optimizeStreamingLatency(),
			voiceClientProperties.header().outputFormat(),
			new VoiceCreateRequestDto(text, voiceClientProperties));
	}

}
