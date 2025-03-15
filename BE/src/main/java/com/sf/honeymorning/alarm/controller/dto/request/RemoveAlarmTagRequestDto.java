package com.sf.honeymorning.alarm.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RemoveAlarmTagRequestDto(
	@NotNull
	@NotBlank
	String word
) {
}
