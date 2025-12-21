package com.honeymorning.api.user.application.port.out;

import com.honeymorning.api.user.application.domain.AuthenticateAccount;

public interface LoadAccountPort {
	AuthenticateAccount getAccount(String username);
}
