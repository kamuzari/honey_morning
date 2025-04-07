package com.sf.honeymorning.user.application.port.in;

import com.sf.honeymorning.user.adapter.in.web.dto.request.LoginAuthRequestDto;
import com.sf.honeymorning.user.adapter.in.web.dto.response.LoginAuthResponseDto;
import com.sf.honeymorning.user.adapter.in.web.dto.response.LogoutAuthResponseDto;

public interface AuthenticateUseCase {
	LoginAuthResponseDto login(LoginAuthRequestDto loginDto);

	LogoutAuthResponseDto logout(Long id);
}
