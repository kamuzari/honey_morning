package com.sf.honeymorning.brief.adapter.in.event.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BriefingSearchCommandDto(@NotNull @Positive Long briefingId) {

}
