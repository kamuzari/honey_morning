package com.sf.honeymorning.authentication.filter;

import java.io.IOException;

import org.springframework.web.filter.GenericFilterBean;

import com.sf.honeymorning.authentication.util.JWTUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomLogoutFilter extends GenericFilterBean {

	private final JWTUtil jwtUtil;

	public CustomLogoutFilter(JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {
		doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
	}

	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
		IOException,
		ServletException {
		//path and method verify
		String requestUri = request.getRequestURI();
		if (!requestUri.matches("^\\/api\\/auth\\/logout$")) {

			filterChain.doFilter(request, response);
			return;
		}
		String requestMethod = request.getMethod();
		if (!requestMethod.equals("POST")) {

			filterChain.doFilter(request, response);
			return;
		}

		//get refresh token
		String refresh = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {

			if (cookie.getName().equals("refresh")) {

				refresh = cookie.getValue();
			}
		}

		//refresh null check
		if (refresh == null) {
			log.info("refresh token이 없습니다. refresh: {}", refresh);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		//expired check
		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {

			log.info("refresh token이 만료되었습니다. refresh: {}", refresh);
			//response status code
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		String category = jwtUtil.getCategory(refresh);
		if (!category.equals("refresh")) {
			log.info("refresh token이 올바르지 않습니다. refresh: {}", refresh);
			//response status code
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
	}
}
