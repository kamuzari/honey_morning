package com.honeymorning.api.alarm.adapter.in.web.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddAlarmResultRequestDto(
	@NotNull
	Long briefingId,

	@NotNull
	@Min(0)
	@Max(2)
	Integer matchCount) {
}
