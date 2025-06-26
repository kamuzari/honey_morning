package com.sf.honeymorning.brief.application.port.out;

import com.sf.honeymorning.brief.application.domain.RetryingFailTts;

public interface TtsCompensationPort {
	RetryingFailTts loadTopOnSkipLock();
	void reflect(RetryingFailTts failTts);
}
