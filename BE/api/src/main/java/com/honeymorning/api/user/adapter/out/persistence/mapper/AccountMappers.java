package com.honeymorning.api.user.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.honeymorning.api.user.adapter.out.persistence.entity.UserEntity;
import com.honeymorning.api.user.application.domain.AuthenticateAccount;

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
