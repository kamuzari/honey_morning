package com.sf.honeymorning.account.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record LoginAuthResponseDto(
	@JsonIgnore
	TokenResponseDto accessToken,

	@JsonIgnore
	TokenResponseDto refreshToken
) {
}
