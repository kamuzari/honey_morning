package com.sf.honeymorning.account.dto.response;

public record TokenResponseDto(
	String header,
	String token,
	long expirySeconds
) {
}
