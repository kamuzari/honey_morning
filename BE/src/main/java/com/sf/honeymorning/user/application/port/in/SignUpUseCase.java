package com.sf.honeymorning.user.application.port.in;

import com.sf.honeymorning.user.adapter.in.web.dto.request.AccountSignUpRequest;

public interface SignUpUseCase {
	void register(AccountSignUpRequest requestDto);

	boolean isUsable(String email);
}
