package com.sf.honeymorning.account.authenticater.jwt;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sf.honeymorning.account.authenticater.constant.JwtProperty;
import com.sf.honeymorning.account.authenticater.exception.TokenNotFoundException;
import com.sf.honeymorning.account.authenticater.service.TokenService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Component
public class JwtProviderManager {

	private final JwtProperty jwtProperty;
	private final Algorithm algorithm;
	private final JWTVerifier jwtVerifier;
	private final TokenService tokenService;

	public JwtProviderManager(JwtProperty jwtProperty, TokenService tokenService) {
		this.jwtProperty = jwtProperty;
		this.tokenService = tokenService;
		this.algorithm = Algorithm.HMAC512(jwtProperty.secretKey());
		this.jwtVerifier = JWT.require(algorithm)
			.withIssuer(this.jwtProperty.issuer())
			.build();
	}

	public String generateAccessToken(CustomClaim customClaim) {
		JWTCreator.Builder jwtBuilder = JWT.create();
		Date now = new Date();

		jwtBuilder.withSubject(customClaim.userId.toString());
		jwtBuilder.withIssuer(this.jwtProperty.issuer());
		jwtBuilder.withIssuedAt(now);
		jwtBuilder.withExpiresAt(new Date(now.getTime() + jwtProperty.accessToken().expirySeconds()));
		jwtBuilder.withClaim("userId", customClaim.userId);
		jwtBuilder.withArrayClaim("roles", customClaim.roles);

		return jwtBuilder.sign(algorithm);
	}

	public String generateRefreshToken(Long userId) {
		Date now = new Date();

		JWTCreator.Builder jwtBuilder = JWT.create();
		jwtBuilder.withIssuer(this.jwtProperty.issuer());
		jwtBuilder.withIssuedAt(now);
		jwtBuilder.withExpiresAt(new Date(now.getTime() + jwtProperty.refreshToken().expirySeconds()));

		String token = jwtBuilder.sign(this.algorithm);
		tokenService.saveRefreshToken(userId, token, jwtProperty.refreshToken().expirySeconds());

		return token;
	}

	public String extractRefreshToken(HttpServletRequest request) {
		if (request.getCookies() == null) {
			throw new TokenNotFoundException("RefreshToken not found");
		}

		return Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals(jwtProperty.refreshToken().header()))
			.findFirst()
			.map(Cookie::getValue)
			.orElseThrow(() -> new TokenNotFoundException("refresh token value null."));
	}

	public String extractAccessToken(HttpServletRequest request) {
		if (request.getCookies() == null) {
			throw new TokenNotFoundException("AccessToken not found");
		}

		return Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals(jwtProperty.accessToken().header()))
			.findFirst()
			.map(Cookie::getValue)
			.orElseThrow(() -> new TokenNotFoundException("access token value null."));
	}

	public List<GrantedAuthority> getAuthorities(CustomClaim claims) {
		String[] roles = claims.roles;

		return roles.length == 0 ? Collections.emptyList() :
			Arrays.stream(roles)
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

	public CustomClaim verify(String accessToken) {
		DecodedJWT decodedJWT = this.jwtVerifier.verify(accessToken);

		return new CustomClaim(decodedJWT);
	}

	public void verifyRefreshToken(String accessToken, String refreshToken) {
		Long userId = decode(accessToken).userId;
		String savedReFreshToken = tokenService.findRefreshTokenByUserId(userId);

		if (!refreshToken.equals(savedReFreshToken)) {
			throw new JWTVerificationException("not match refresh token.");
		}
	}

	public CustomClaim decode(String accessToken) {
		return new CustomClaim(JWT.decode(accessToken));
	}

	public void removeRefreshToken(Long id) {
		tokenService.remove(id);
	}

	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@EqualsAndHashCode
	public static class CustomClaim {
		Long userId;
		String[] roles;
		Date issuedAt;
		Date expiredAt;

		CustomClaim(DecodedJWT decodedJWT) {
			Claim userId = decodedJWT.getClaim("userId");

			if (!userId.isNull()) {
				this.userId = userId.asLong();
			}

			Claim roles = decodedJWT.getClaim("roles");

			if (!roles.isNull()) {
				this.roles = roles.asArray(String.class);
			}

			this.issuedAt = decodedJWT.getIssuedAt();
			this.expiredAt = decodedJWT.getExpiresAt();
		}
	}
}
