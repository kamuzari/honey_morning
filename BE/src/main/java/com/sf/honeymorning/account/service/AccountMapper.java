package com.sf.honeymorning.account.service;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.account.dto.response.LoginAuthResponseDto;
import com.sf.honeymorning.account.dto.response.LogoutAuthResponseDto;
import com.sf.honeymorning.account.dto.response.TokenResponseDto;
import com.sf.honeymorning.account.authenticater.constant.JwtProperty;

@Component
public class AccountMapper {
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
