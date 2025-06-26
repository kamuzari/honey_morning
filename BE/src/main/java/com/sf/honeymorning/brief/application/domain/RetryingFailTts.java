package com.sf.honeymorning.brief.application.domain;

import com.sf.honeymorning.brief.adapter.out.persistence.entity.event.EventStatus;

import lombok.Getter;

@Getter
public class RetryingFailTts {
	public static final long EMPTY = 0L;

	private Long failTtsEventId;
	private Long failBriefingId;
	private EventStatus eventStatus;

	public static RetryingFailTts createEmpty() {
		return new RetryingFailTts(EMPTY, null, null);
	}

	public RetryingFailTts(Long failTtsEventId, Long failBriefingId, EventStatus eventStatus) {
		this.failTtsEventId = failTtsEventId;
		this.failBriefingId = failBriefingId;
		this.eventStatus = eventStatus;
	}

	public void complete() {
		this.eventStatus = EventStatus.RETRY_COMPLETED;
	}

	public boolean isEmpty() {
		return failTtsEventId == EMPTY;
	}
}
