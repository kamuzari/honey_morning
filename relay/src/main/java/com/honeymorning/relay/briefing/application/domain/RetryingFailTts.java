package com.honeymorning.relay.briefing.application.domain;

import com.honeymorning.common.common.basic.EventStatus;

import lombok.Getter;

@Getter
public class RetryingFailTts {
	private static final long EMPTY = 0L;

	private Long failTtsEventId;
	private Long failBriefingId;
	private EventStatus eventStatus;

	public RetryingFailTts(Long failTtsEventId, Long failBriefingId, EventStatus eventStatus) {
		this.failTtsEventId = failTtsEventId;
		this.failBriefingId = failBriefingId;
		this.eventStatus = eventStatus;
	}

	public static RetryingFailTts createEmpty() {
		return new RetryingFailTts(EMPTY, null, null);
	}

	public void complete() {
		this.eventStatus = EventStatus.RETRY_COMPLETED;
	}

	public boolean isEmpty() {
		return failTtsEventId == EMPTY;
	}
}
