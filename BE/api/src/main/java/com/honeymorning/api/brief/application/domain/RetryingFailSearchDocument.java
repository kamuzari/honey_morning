package com.honeymorning.api.brief.application.domain;

import com.honeymorning.api.common.entity.basic.EventStatus;

import lombok.Getter;

@Getter
public class RetryingFailSearchDocument {
	private static final long EMPTY = 0L;

	private Long failSearchEventId;
	private Long briefingId;
	private EventStatus eventStatus;

	public static RetryingFailSearchDocument createEmpty() {
		return new RetryingFailSearchDocument(EMPTY, null, null);
	}

	public RetryingFailSearchDocument(Long failSearchEventId, Long briefingId, EventStatus eventStatus) {
		this.failSearchEventId = failSearchEventId;
		this.briefingId = briefingId;
		this.eventStatus = eventStatus;
	}

	public void complete() {
		this.eventStatus = EventStatus.RETRY_COMPLETED;
	}

	public boolean isEmpty() {
		return failSearchEventId == EMPTY;
	}
}
