package com.sf.honeymorning.brief.adapter.out.external;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.sf.honeymorning.brief.adapter.out.external.api.VoiceClient;
import com.sf.honeymorning.brief.adapter.out.external.dto.request.VoiceCreateRequestDto;
import com.sf.honeymorning.brief.application.port.out.CommandTtsPort;
import com.sf.honeymorning.config.constant.VoiceClientProperties;

@Component
public class TtsClientAdapter implements CommandTtsPort {

	private final VoiceClient voiceClient;
	private final VoiceClientProperties voiceClientProperties;

	public TtsClientAdapter(VoiceClient voiceClient, VoiceClientProperties voiceClientProperties) {
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
