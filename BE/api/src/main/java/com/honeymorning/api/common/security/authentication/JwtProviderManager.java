package com.honeymorning.api.common.security.authentication;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.honeymorning.api.common.security.authentication.constant.JwtProperty;
import com.honeymorning.api.common.security.authentication.helper.JwtTokenGenerator;
import com.honeymorning.api.common.security.authentication.helper.JwtTokenWebExtractor;
import com.honeymorning.api.common.security.core.JwtClaim;
import com.honeymorning.api.common.security.authentication.service.TokenService;
import com.honeymorning.api.user.adapter.out.persistence.entity.UserRole;
import com.honeymorning.api.user.application.port.out.TokenGeneratePort;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtProviderManager implements TokenGeneratePort {

	private final JwtProperty jwtProperty;
	private final JWTVerifier jwtVerifier;
	private final TokenService tokenService;
	private final JwtTokenGenerator jwtTokenGenerator;
	private final JwtTokenWebExtractor jwtTokenWebExtractor;

	public JwtProviderManager(
		JwtProperty jwtProperty,
		TokenService tokenService,
		JwtTokenGenerator jwtTokenGenerator,
		JwtTokenWebExtractor jwtTokenWebExtractor) {

		this.jwtProperty = jwtProperty;
		this.tokenService = tokenService;
		this.jwtTokenGenerator = jwtTokenGenerator;
		this.jwtTokenWebExtractor = jwtTokenWebExtractor;

		Algorithm algorithm = Algorithm.HMAC512(jwtProperty.secretKey());
		this.jwtVerifier = JWT.require(algorithm)
			.withIssuer(this.jwtProperty.issuer())
			.build();
	}

	public String generateAccessToken(Long userId, UserRole role) {
		return jwtTokenGenerator.generateAccessToken(userId, role);
	}

	public String generateAccessToken(JwtClaim jwtClaim) {
		return jwtTokenGenerator.generateAccessToken(jwtClaim);
	}

	public String generateRefreshToken(Long userId) {
		String refreshToken = jwtTokenGenerator.generateRefreshToken(userId);
		tokenService.saveRefreshToken(userId, refreshToken, jwtProperty.refreshToken().expirySeconds());

		return refreshToken;
	}

	public String extractRefreshToken(HttpServletRequest request) {
		return jwtTokenWebExtractor.extractRefreshToken(request);
	}

	public String extractAccessToken(HttpServletRequest request) {
		return jwtTokenWebExtractor.extractAccessToken(request);
	}

	public List<GrantedAuthority> getAuthorities(JwtClaim claims) {
		String[] roles = claims.getRoles();

		return roles.length == 0 ? Collections.emptyList() :
			Arrays.stream(roles)
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

	public JwtClaim verify(String accessToken) {
		DecodedJWT decodedJWT = this.jwtVerifier.verify(accessToken);

		return new JwtClaim(decodedJWT);
	}

	public void verifyRefreshToken(String accessToken, String refreshToken) {
		JwtClaim claim = new JwtClaim(JWT.decode(accessToken));
		Long userId = claim.getUserId();

		String savedReFreshToken = tokenService.findRefreshTokenByUserId(userId);

		if (savedReFreshToken.isBlank()) {
			throw new TokenExpiredException("token expired retry login");
		}

		if (!refreshToken.equals(savedReFreshToken)) {
			throw new JWTVerificationException("not match refresh token.");
		}
	}

	public void removeRefreshToken(Long id) {
		tokenService.remove(id);
	}

}
