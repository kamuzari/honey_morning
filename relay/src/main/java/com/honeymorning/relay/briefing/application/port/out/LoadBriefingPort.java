package com.honeymorning.relay.briefing.application.port.out;

import com.honeymorning.relay.briefing.application.domain.TextToSpeechContent;

public interface LoadBriefingPort {
	TextToSpeechContent getTtsBriefingWithQuizzes(Long id);
}
