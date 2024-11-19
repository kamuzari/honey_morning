package com.sf.honeymorning.account.dto.response;

public record LogoutAuthResponseDto(
	String accessTokenHeader,
	String refreshTokenHeader
) {
}
