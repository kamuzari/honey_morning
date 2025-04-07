package com.sf.honeymorning.user.adapter.in.web.dto.response;

public record LogoutAuthResponseDto(
	String accessTokenHeader,
	String refreshTokenHeader
) {
}
