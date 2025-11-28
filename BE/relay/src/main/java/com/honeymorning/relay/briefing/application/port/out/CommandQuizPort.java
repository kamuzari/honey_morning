package com.honeymorning.relay.briefing.application.port.out;

import com.honeymorning.relay.briefing.application.domain.EmptyQuizTts;

public interface CommandQuizPort {
	void reflect(EmptyQuizTts emptyQuizTts);
}
