package com.honeymorning.api.user.application.port.in;

import com.honeymorning.api.user.adapter.in.web.dto.request.AccountSignUpRequest;

public interface SignUpUseCase {
	void register(AccountSignUpRequest requestDto);

	boolean isUsable(String email);
}
