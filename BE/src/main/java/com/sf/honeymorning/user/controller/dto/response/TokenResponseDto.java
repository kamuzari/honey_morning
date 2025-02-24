package com.sf.honeymorning.user.controller.dto.response;

public record TokenResponseDto(
	String header,
	String token,
	long expirySeconds
) {
}
