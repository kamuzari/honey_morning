package com.honeymorning.api.user.application.port.in;

import com.honeymorning.api.user.adapter.in.web.dto.request.LoginAuthRequestDto;
import com.honeymorning.api.user.adapter.in.web.dto.response.LoginAuthResponseDto;
import com.honeymorning.api.user.adapter.in.web.dto.response.LogoutAuthResponseDto;

public interface AuthenticateUseCase {
	LoginAuthResponseDto login(LoginAuthRequestDto loginDto);

	LogoutAuthResponseDto logout(Long id);
}
