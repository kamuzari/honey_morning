package com.honeymorning.api.user.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AccountSignUpRequest(
	@NotBlank
	@Size(min = 8, max = 55)
	String username,

	@NotBlank
	@Size(min = 8, max = 32)
	String rawPassword,

	@Size(min = 3, max = 14)
	@NotBlank
	String nickName
) {
}
