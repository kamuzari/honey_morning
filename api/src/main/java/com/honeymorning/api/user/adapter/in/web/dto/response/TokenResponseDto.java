package com.honeymorning.api.user.adapter.in.web.dto.response;

public record TokenResponseDto(
	String header,
	String token,
	long expirySeconds
) {
}
