package com.honeymorning.api.user.adapter.in.web.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.honeymorning.api.common.security.authentication.constant.CookieProperty;
import com.honeymorning.api.user.adapter.in.web.dto.response.LoginAuthResponseDto;
import com.honeymorning.api.user.adapter.in.web.dto.response.TokenResponseDto;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticateSuccessHandler {
	private final CookieProperty cookieProperty;

	public AuthenticateSuccessHandler(CookieProperty cookieProperty) {
		this.cookieProperty = cookieProperty;
	}

	public void onLoginSuccess(HttpServletResponse response, LoginAuthResponseDto loginResponse) {
		ResponseCookie accessCookie = createCookie(loginResponse.accessToken());
		ResponseCookie refreshCookie = createCookie(loginResponse.refreshToken());

		response.setHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
	}

	private ResponseCookie createCookie(TokenResponseDto tokenResponse) {
		return ResponseCookie.from(tokenResponse.header(), tokenResponse.token())
			.path("/")
			.httpOnly(true)
			.secure(cookieProperty.secure())
			.domain(cookieProperty.domain())
			.maxAge(tokenResponse.expirySeconds())
			.sameSite(cookieProperty.sameSite().attributeValue())
			.build();
	}
}


