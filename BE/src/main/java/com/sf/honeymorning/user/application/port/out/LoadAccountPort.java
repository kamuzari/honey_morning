package com.sf.honeymorning.user.application.port.out;

import com.sf.honeymorning.user.application.domain.AuthenticateAccount;

public interface LoadAccountPort {
	AuthenticateAccount getAccount(String username);
}
