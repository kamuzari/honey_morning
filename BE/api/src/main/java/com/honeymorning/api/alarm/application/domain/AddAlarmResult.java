package com.honeymorning.api.alarm.application.domain;

import static com.honeymorning.api.alarm.common.AlarmResultConstraint.MATCH_COUNT_MAXIMUM_VALUE;
import static com.honeymorning.api.alarm.common.AlarmResultConstraint.MATCH_COUNT_MINIMUM_VALUE;

public record AddAlarmResult(
	Long userId,
	Long briefingId,
	int matchCount,
	boolean isAttended
) {
	public AddAlarmResult {
		if (matchCount < MATCH_COUNT_MINIMUM_VALUE || matchCount > MATCH_COUNT_MAXIMUM_VALUE) {
			throw new IllegalArgumentException("맞은 개수는 반드시 [0=2] 사이여야 합니다");
		}

		if (!isAttended && matchCount > 0) {
			throw new IllegalStateException("참석이 활성화 되지 않은 채로 match count가 0보다 클 수 없습니다");
		}
	}
}
