package com.sf.honeymorning.account.authenticater.model;

import org.springframework.util.Assert;

public record JwtAuthentication(Long id, String token) {
	public JwtAuthentication {
		Assert.notNull(id, "user id is required elements");

		if (token.isBlank()) {
			throw new IllegalArgumentException("token is required elements");
		}
	}
}
