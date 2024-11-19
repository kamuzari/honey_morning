package com.sf.honeymorning.account.handler;

import static org.springframework.http.HttpHeaders.SET_COOKIE;


import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.sf.honeymorning.account.authenticater.constant.CookieProperty;
import com.sf.honeymorning.account.dto.response.LogoutAuthResponseDto;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class LogoutSuccessHandler {
	private final CookieProperty cookieProperty;

	public void onLogoutSuccess(HttpServletResponse response, LogoutAuthResponseDto logoutResponse) {
		Assert.notNull(logoutResponse.accessTokenHeader(), "access token header is cannot be null");
		Assert.notNull(logoutResponse.accessTokenHeader(), "refresh token header is cannot be null");

		ResponseCookie accessTokenCookie = createCookie(logoutResponse.accessTokenHeader());
		ResponseCookie refreshTokenCookie = createCookie(logoutResponse.refreshTokenHeader());

		response.setHeader(SET_COOKIE, accessTokenCookie.toString());
		response.addHeader(SET_COOKIE, refreshTokenCookie.toString());
	}

	private ResponseCookie createCookie(String header) {
		return ResponseCookie.from(header, "")
			.path("/")
			.httpOnly(true)
			.secure(cookieProperty.secure())
			.domain(cookieProperty.domain())
			.maxAge(0)
			.build();
	}
}
