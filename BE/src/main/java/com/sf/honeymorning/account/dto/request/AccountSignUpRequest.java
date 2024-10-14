package com.sf.honeymorning.account.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AccountSignUpRequest(
	@NotBlank
	String username,

	@Min(8)
	@Max(24)
	@NotBlank
	String rawPassword,

	@NotBlank
	String nickName
) {
}
