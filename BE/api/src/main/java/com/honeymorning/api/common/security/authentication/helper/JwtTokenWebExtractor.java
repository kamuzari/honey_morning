package com.honeymorning.api.common.security.authentication.helper;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import com.honeymorning.api.common.security.authentication.constant.JwtProperty;
import com.honeymorning.api.common.security.authentication.exception.TokenNotFoundException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtTokenWebExtractor {
	private final JwtProperty jwtProperty;

	public JwtTokenWebExtractor(JwtProperty jwtProperty) {
		this.jwtProperty = jwtProperty;
	}

	public String extractRefreshToken(HttpServletRequest request) {
		if (request.getCookies() == null) {
			throw new TokenNotFoundException("RefreshToken not found");
		}

		return Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals(jwtProperty.refreshToken().header()))
			.findFirst()
			.map(Cookie::getValue)
			.orElseThrow(() -> new TokenNotFoundException("refresh token value null"));
	}

	public String extractAccessToken(HttpServletRequest request) {
		if (request.getCookies() == null) {
			throw new TokenNotFoundException("AccessToken not found");
		}

		return Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals(jwtProperty.accessToken().header()))
			.findFirst()
			.map(Cookie::getValue)
			.orElseThrow(() -> new TokenNotFoundException("token value null"));
	}
}
