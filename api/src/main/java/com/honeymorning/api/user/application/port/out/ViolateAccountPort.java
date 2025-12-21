package com.honeymorning.api.user.application.port.out;

public interface ViolateAccountPort {
	void duplicate(String username);

	boolean isExist(String username);
}
