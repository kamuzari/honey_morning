package com.honeymorning.relay.briefing.application.port.out;

import com.honeymorning.relay.briefing.application.domain.RetryingFailTts;

public interface TtsCompensationPort {
	RetryingFailTts loadTopOnSkipLock();

	void reflect(RetryingFailTts failTts);
}
