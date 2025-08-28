package com.honeymorning.api.brief.application.port.out;

import com.honeymorning.api.brief.application.domain.RetryingFailSearchDocument;

public interface SearchCompensationPort {
	void compensate(RetryingFailSearchDocument retryingFailSearchDocument);
}
