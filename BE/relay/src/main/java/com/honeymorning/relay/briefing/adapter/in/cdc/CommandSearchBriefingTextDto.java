package com.honeymorning.relay.briefing.adapter.in.cdc;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CommandSearchBriefingTextDto(
	@JsonProperty("id") Long briefingId
) {
}
