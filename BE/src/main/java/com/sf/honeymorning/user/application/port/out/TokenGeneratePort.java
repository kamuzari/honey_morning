package com.sf.honeymorning.user.application.port.out;

import com.sf.honeymorning.user.adapter.out.persistence.entity.UserRole;

public interface TokenGeneratePort {
	String generateAccessToken(Long userId, UserRole role);

	String generateRefreshToken(Long userId);

	void removeRefreshToken(Long id);
}
