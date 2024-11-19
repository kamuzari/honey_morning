package com.sf.honeymorning.account.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AccountSignUpRequest(
	@NotBlank
	String username,

	@NotBlank
	String rawPassword,

	@NotBlank
	String nickName
) {
}
