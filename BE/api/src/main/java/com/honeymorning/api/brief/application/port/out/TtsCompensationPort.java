package com.honeymorning.api.brief.application.port.out;

import com.honeymorning.api.brief.application.domain.RetryingFailTts;

public interface TtsCompensationPort {
	RetryingFailTts loadTopOnSkipLock();
	void reflect(RetryingFailTts failTts);
}
