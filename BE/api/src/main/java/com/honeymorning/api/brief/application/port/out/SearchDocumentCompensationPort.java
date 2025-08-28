package com.honeymorning.api.brief.application.port.out;

import com.honeymorning.api.brief.application.domain.RetryingFailSearchDocument;

public interface SearchDocumentCompensationPort {
	RetryingFailSearchDocument loadTopOnSkipLock();
	void reflect(RetryingFailSearchDocument retryingFailSearchDocument);
}
