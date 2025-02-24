package com.sf.honeymorning.user.controller.dto.response;

public record LogoutAuthResponseDto(
	String accessTokenHeader,
	String refreshTokenHeader
) {
}
