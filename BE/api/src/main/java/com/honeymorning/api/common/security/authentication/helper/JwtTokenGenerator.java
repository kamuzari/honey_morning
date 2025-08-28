package com.honeymorning.api.common.security.authentication.helper;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.honeymorning.api.common.security.authentication.constant.JwtProperty;
import com.honeymorning.api.common.security.core.JwtClaim;
import com.honeymorning.api.user.adapter.out.persistence.entity.UserRole;

@Component
public class JwtTokenGenerator {
	private final JwtProperty jwtProperty;
	private final Algorithm algorithm;

	public JwtTokenGenerator(JwtProperty jwtProperty) {
		this.jwtProperty = jwtProperty;
		this.algorithm = Algorithm.HMAC512(jwtProperty.secretKey());
	}

	public String generateAccessToken(Long userId, UserRole role) {
		JwtClaim claim = JwtClaim.builder()
			.userId(userId)
			.roles(new String[] {role.name()})
			.build();

		return this.generateAccessToken(claim);
	}

	public String generateAccessToken(JwtClaim jwtClaim) {
		JWTCreator.Builder jwtBuilder = JWT.create();
		Date now = new Date();

		jwtBuilder.withSubject(jwtClaim.getUserId().toString());
		jwtBuilder.withIssuer(this.jwtProperty.issuer());
		jwtBuilder.withIssuedAt(now);
		jwtBuilder.withExpiresAt(new Date(now.getTime() + jwtProperty.accessToken().expirySeconds()));
		jwtBuilder.withClaim("userId", jwtClaim.getUserId());
		jwtBuilder.withArrayClaim("roles", jwtClaim.getRoles());

		return jwtBuilder.sign(algorithm);
	}

	public String generateRefreshToken(Long userId) {
		Date now = new Date();

		JWTCreator.Builder jwtBuilder = JWT.create();
		jwtBuilder.withSubject(userId.toString());
		jwtBuilder.withIssuer(this.jwtProperty.issuer());
		jwtBuilder.withIssuedAt(now);
		jwtBuilder.withExpiresAt(new Date(now.getTime() + jwtProperty.refreshToken().expirySeconds()));

		return jwtBuilder.sign(this.algorithm);
	}
}
