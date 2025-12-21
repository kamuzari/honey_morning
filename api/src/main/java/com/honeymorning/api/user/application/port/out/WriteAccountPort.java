package com.honeymorning.api.user.application.port.out;

public interface WriteAccountPort {
	void create(String username,
		String encryptedPassword,
		String nickName);
}
