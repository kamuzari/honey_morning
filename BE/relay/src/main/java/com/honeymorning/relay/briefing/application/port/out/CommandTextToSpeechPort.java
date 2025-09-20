package com.honeymorning.relay.briefing.application.port.out;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface CommandTextToSpeechPort {
	ResponseEntity<Resource> create(String text);
}
