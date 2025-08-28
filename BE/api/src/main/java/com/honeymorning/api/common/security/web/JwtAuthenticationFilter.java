package com.honeymorning.api.common.security.web;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.honeymorning.api.common.security.authentication.constant.CookieProperty;
import com.honeymorning.api.common.security.authentication.constant.JwtProperty;
import com.honeymorning.api.common.security.authentication.exception.TokenNotFoundException;
import com.honeymorning.api.common.security.authentication.JwtProviderManager;
import com.honeymorning.api.common.security.core.JwtClaim;
import com.honeymorning.api.common.security.core.JwtAuthentication;
import com.honeymorning.api.common.security.core.JwtAuthenticationToken;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	
	private final JwtProviderManager jwtProviderManager;
	private final JwtProperty jwtProperty;
	private final CookieProperty cookieProperty;

	public JwtAuthenticationFilter(
		JwtProviderManager jwtProviderManager,
		JwtProperty jwtProperty,
		CookieProperty cookieProperty) {

		this.jwtProviderManager = jwtProviderManager;
		this.jwtProperty = jwtProperty;
		this.cookieProperty = cookieProperty;
	}

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			String accessToken = jwtProviderManager.extractAccessToken(request);
			attemptAuthenticate(accessToken, request, response);
		} catch (TokenNotFoundException e) {
			log.warn("token is not exist..");
		}

		filterChain.doFilter(request, response);
	}

	private void attemptAuthenticate(String accessToken, HttpServletRequest request, HttpServletResponse response) {
		try {
			JwtClaim verifiedClaim = jwtProviderManager.verify(accessToken);
			JwtAuthenticationToken authenticationToken = createAuthenticationToken(verifiedClaim, request, accessToken);
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);

		} catch (TokenExpiredException e) {
			log.warn(e.getMessage());
			reIssueAccessToken(accessToken, request, response);
		} catch (JWTVerificationException e) {
			log.warn(e.getMessage());
		}
	}

	private void reIssueAccessToken(
		String accessToken,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		try {
			String refreshToken = jwtProviderManager.extractRefreshToken(request);
			jwtProviderManager.verifyRefreshToken(accessToken, refreshToken);
			JwtClaim claim = new JwtClaim(JWT.decode(accessToken));
			String reIssuedToken = jwtProviderManager.generateAccessToken(claim);
			JwtClaim verifiedClaim = jwtProviderManager.verify(reIssuedToken);
			var authenticationToken = createAuthenticationToken(verifiedClaim, request, reIssuedToken);

			ResponseCookie cookie = ResponseCookie.from(jwtProperty.accessToken().header(), reIssuedToken)
				.path("/")
				.httpOnly(true)
				.sameSite(cookieProperty.sameSite().attributeValue())
				.domain(cookieProperty.domain())
				.secure(cookieProperty.secure())
				.maxAge(jwtProperty.refreshToken().expirySeconds())
				.build();

			response.addHeader(SET_COOKIE, cookie.toString());
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		} catch (EntityNotFoundException | TokenNotFoundException | JWTVerificationException e) {
			log.warn("refresh token expire. try login");
		}
	}

	private JwtAuthenticationToken createAuthenticationToken(
		JwtClaim claims,
		HttpServletRequest request,
		String accessToken
	) {
		List<GrantedAuthority> authorities = jwtProviderManager.getAuthorities(claims);

		if (claims.getUserId() == null || authorities.isEmpty()) {
			throw new JWTDecodeException("Decode Error");
		}

		JwtAuthentication authentication = new JwtAuthentication(claims.getUserId(), accessToken);
		JwtAuthenticationToken authenticationToken = JwtAuthenticationToken.create(authentication, authorities);
		authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

		return authenticationToken;
	}
}
