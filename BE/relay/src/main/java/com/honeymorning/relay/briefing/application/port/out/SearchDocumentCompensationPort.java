package com.honeymorning.relay.briefing.application.port.out;

import com.honeymorning.relay.briefing.application.domain.RetryingFailSearchDocument;

public interface SearchDocumentCompensationPort {

	RetryingFailSearchDocument loadTopOnSkipLock();

	void reflect(RetryingFailSearchDocument retryingFailSearchDocument);
}
