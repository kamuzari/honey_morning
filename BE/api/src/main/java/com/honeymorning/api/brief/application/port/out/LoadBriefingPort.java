package com.honeymorning.api.brief.application.port.out;

import com.honeymorning.api.brief.application.domain.TextToSpeechContent;

public interface LoadBriefingPort {
	TextToSpeechContent getTtsBriefingWithQuizzes(Long id);
}
