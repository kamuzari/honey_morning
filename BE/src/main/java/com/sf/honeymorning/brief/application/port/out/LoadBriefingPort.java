package com.sf.honeymorning.brief.application.port.out;

import com.sf.honeymorning.brief.application.domain.TextToSpeechContent;

public interface LoadBriefingPort {
	TextToSpeechContent getTtsBriefingWithQuizzes(Long id);
}
