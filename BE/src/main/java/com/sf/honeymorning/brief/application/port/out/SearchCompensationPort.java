package com.sf.honeymorning.brief.application.port.out;

import com.sf.honeymorning.brief.application.domain.RetryingFailSearchDocument;

public interface SearchCompensationPort {
	void compensate(RetryingFailSearchDocument retryingFailSearchDocument);
}
