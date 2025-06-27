package com.sf.honeymorning.brief.application.port.out;

import com.sf.honeymorning.brief.application.domain.RetryingFailSearchDocument;

public interface SearchDocumentCompensationPort {
	RetryingFailSearchDocument loadTopOnSkipLock();
	void reflect(RetryingFailSearchDocument retryingFailSearchDocument);
}
