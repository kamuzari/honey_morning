package com.honeymorning.relay.briefing.adapter.in.event.dto;


import com.honeymorning.relay.event.compensation.compensator.FailEventTyper;
import com.honeymorning.relay.event.compensation.compensator.FailType;

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
