package com.honeymorning.api.user.application.domain;

import com.honeymorning.api.user.adapter.out.persistence.entity.UserRole;

public record AuthenticateAccount(
	Long id,
	String encryptedPassword,
	UserRole role
) {
}
