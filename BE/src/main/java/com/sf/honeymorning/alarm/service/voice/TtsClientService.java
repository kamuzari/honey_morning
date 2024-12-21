package com.sf.honeymorning.alarm.service.voice;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sf.honeymorning.alarm.client.VoiceClient;
import com.sf.honeymorning.alarm.client.dto.VoiceCreateRequestDto;
import com.sf.honeymorning.config.constant.VoiceClientProperties;

@Service
public class TtsClientService {

	private final VoiceClient voiceClient;
	private final VoiceClientProperties voiceClientProperties;

	public TtsClientService(VoiceClient voiceClient, VoiceClientProperties voiceClientProperties) {
		this.voiceClient = voiceClient;
		this.voiceClientProperties = voiceClientProperties;
	}

	public ResponseEntity<Resource> create(String text) {
		return voiceClient.createTts(
			voiceClientProperties.path().voiceId(),
			voiceClientProperties.header().xiApiKey(),
			voiceClientProperties.header().optimizeStreamingLatency(),
			voiceClientProperties.header().outputFormat(),
			new VoiceCreateRequestDto(text, voiceClientProperties));
	}

}
