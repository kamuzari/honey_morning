package com.honeymorning.api.brief.adapter.in.event.dto;

import com.honeymorning.api.common.event.compensation.model.FailType;
import com.honeymorning.api.common.event.compensation.model.FailEventTyper;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BriefingSearchCommandDto(@NotNull @Positive Long briefingId) implements FailEventTyper {

	@Override
	public Long getIdentifierForReward() {
		return briefingId;
	}

	@Override
	public FailType getFailType() {
		return FailType.SEARCH_FAILURE;
	}
}
