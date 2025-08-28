package com.honeymorning.api.common.security.core;

import java.util.Date;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class JwtClaim {
	private Long userId;
	private String[] roles;
	private Date issuedAt;
	private Date expiredAt;

	public JwtClaim(DecodedJWT decodedJWT) {
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
