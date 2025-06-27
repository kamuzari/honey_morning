package com.sf.honeymorning.brief.adapter.in.event.dto;

import com.sf.honeymorning.common.event.compensation.model.FailEventTyper;
import com.sf.honeymorning.common.event.compensation.model.FailType;

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
