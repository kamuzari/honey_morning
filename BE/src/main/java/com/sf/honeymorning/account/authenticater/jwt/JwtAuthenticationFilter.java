package com.sf.honeymorning.account.authenticater.jwt;

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

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.sf.honeymorning.account.authenticater.constant.CookieProperty;
import com.sf.honeymorning.account.authenticater.constant.JwtProperty;
import com.sf.honeymorning.account.authenticater.exception.TokenNotFoundException;
import com.sf.honeymorning.account.authenticater.jwt.JwtProviderManager.CustomClaim;
import com.sf.honeymorning.account.authenticater.model.JwtAuthentication;
import com.sf.honeymorning.account.authenticater.model.JwtAuthenticationToken;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	private final JwtProviderManager jwtProviderManager;
	private final JwtProperty jwtProperty;
	private final CookieProperty cookieProperty;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		try {
			String accessToken = jwtProviderManager.extractAccessToken(request);
			authenticate(accessToken, request, response);
		} catch (TokenNotFoundException e) {
			logger.warn("token is not exist..");
		} finally {
			filterChain.doFilter(request, response);
		}
	}

	private void authenticate(String accessToken, HttpServletRequest request, HttpServletResponse response) {
		try {
			CustomClaim verifiedClaim = jwtProviderManager.verify(accessToken);
			JwtAuthenticationToken authenticationToken = createAuthenticationToken(verifiedClaim, request, accessToken);
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);

		} catch (TokenExpiredException e) {
			logger.warn(e.getMessage());
			reIssueAccessToken(accessToken, request, response);
		} catch (JWTVerificationException e) {
			logger.warn(e.getMessage());
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
			CustomClaim claim = jwtProviderManager.decode(accessToken);
			String reIssuedToken = jwtProviderManager.generateAccessToken(claim);
			CustomClaim verifiedClaim = jwtProviderManager.verify(reIssuedToken);
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
			logger.warn("refresh token expire. try login");
		}
	}

	private JwtAuthenticationToken createAuthenticationToken(
		CustomClaim claims,
		HttpServletRequest request,
		String accessToken
	) {
		List<GrantedAuthority> authorities = jwtProviderManager.getAuthorities(claims);

		if (claims.userId == null || authorities.isEmpty()) {
			throw new JWTDecodeException("Decode Error");
		}

		JwtAuthentication authentication = new JwtAuthentication(claims.userId, accessToken);
		JwtAuthenticationToken authenticationToken = JwtAuthenticationToken.create(authentication, authorities);
		authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

		return authenticationToken;
	}
}
