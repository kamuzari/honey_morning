package com.sf.honeymorning.alarm.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddAlarmTagRequestDto(
	@NotNull
	@NotBlank
	String word) {
}
