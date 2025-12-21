package com.honeymorning.api.user.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.honeymorning.api.user.application.domain.AuthenticateAccount;
import com.honeymorning.common.domain.user.entity.UserEntity;

@Component
public class AccountMappers {
	public AuthenticateAccount toAccount(UserEntity principal) {
		return new AuthenticateAccount(
			principal.getId(),
			principal.getPassword(),
			principal.getRole()
		);
	}
}
