package com.honeymorning.relay.briefing.application.port.out;

import com.honeymorning.relay.briefing.application.domain.EmptyQuizTts;

public interface LoadQuizPort {
	EmptyQuizTts getEmptyTtsQuiz(Long briefingId, Integer order);
}
