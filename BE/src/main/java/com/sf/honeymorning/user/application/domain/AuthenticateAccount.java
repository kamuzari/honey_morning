package com.sf.honeymorning.user.application.domain;

import com.sf.honeymorning.user.adapter.out.persistence.entity.UserRole;

public record AuthenticateAccount(
	Long id,
	String encryptedPassword,
	UserRole role
) {
}
