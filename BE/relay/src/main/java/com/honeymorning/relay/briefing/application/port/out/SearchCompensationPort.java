package com.honeymorning.relay.briefing.application.port.out;

import com.honeymorning.relay.briefing.application.domain.RetryingFailSearchDocument;

public interface SearchCompensationPort {
	void compensate(RetryingFailSearchDocument retryingFailSearchDocument);
}
