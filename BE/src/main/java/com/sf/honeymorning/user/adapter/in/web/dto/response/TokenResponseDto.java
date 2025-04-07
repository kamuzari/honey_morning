package com.sf.honeymorning.user.adapter.in.web.dto.response;

public record TokenResponseDto(
	String header,
	String token,
	long expirySeconds
) {
}
