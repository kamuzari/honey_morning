package com.sf.honeymorning.common.security.authentication.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import com.sf.honeymorning.common.security.core.Token;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperty(
	Token accessToken,
	Token refreshToken,
	String issuer,
	String secretKey
) {
	@ConstructorBinding
	public JwtProperty {
	}
}
