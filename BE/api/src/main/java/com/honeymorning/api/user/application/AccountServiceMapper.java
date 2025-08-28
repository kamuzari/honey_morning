package com.honeymorning.api.user.application;

import org.springframework.stereotype.Component;

import com.honeymorning.api.common.security.authentication.constant.JwtProperty;
import com.honeymorning.api.user.adapter.in.web.dto.response.LoginAuthResponseDto;
import com.honeymorning.api.user.adapter.in.web.dto.response.LogoutAuthResponseDto;
import com.honeymorning.api.user.adapter.in.web.dto.response.TokenResponseDto;

@Component
public class AccountServiceMapper {
	public LoginAuthResponseDto toLoginResponse(String accessToken, String refreshToken, JwtProperty jwtProperty) {
		return new LoginAuthResponseDto(
			new TokenResponseDto(
				jwtProperty.accessToken().header(),
				accessToken,
				jwtProperty.accessToken().expirySeconds()
			),
			new TokenResponseDto(
				jwtProperty.refreshToken().header(),
				refreshToken,
				jwtProperty.refreshToken().expirySeconds()
			)
		);
	}

	public LogoutAuthResponseDto toLogoutResponse(JwtProperty jwtProperty) {
		return new LogoutAuthResponseDto(
			jwtProperty.accessToken().header(),
			jwtProperty.refreshToken().header()
		);
	}
}
