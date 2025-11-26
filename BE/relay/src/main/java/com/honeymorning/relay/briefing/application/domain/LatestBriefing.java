package com.honeymorning.relay.briefing.application.domain;

import java.time.Duration;
import java.time.LocalDateTime;

public record LatestBriefing(Long briefingId, LocalDateTime createdAt) {

	public LatestBriefing {
		if (createdAt == null) {
			throw new IllegalArgumentException("CreatedAt cannot be null");
		}

		Duration duration = Duration.between(LocalDateTime.now(), createdAt);
		boolean isNotLatest = duration.toHours() > 1;
		if(isNotLatest){
			throw new IllegalArgumentException("방금 생성된 브리핑 데이터가 아닙니다.");
		}
	}
}
