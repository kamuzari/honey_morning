package com.honeymorning.api.brief.adapter.in.event.dto;

import com.honeymorning.api.common.event.compensation.model.FailEventTyper;
import com.honeymorning.api.common.event.compensation.model.FailType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BriefingTtsCommandDto(@NotNull @Positive Long briefingId) implements FailEventTyper {
	@Override
	public Long getIdentifierForReward() {
		return this.briefingId;
	}

	@Override
	public FailType getFailType() {
		return FailType.TTS_FAILURE;
	}
}
