package com.honeymorning.api.user.application.domain;

import com.honeymorning.common.domain.user.entity.UserRole;

public record AuthenticateAccount(
	Long id,
	String encryptedPassword,
	UserRole role
) {
}
