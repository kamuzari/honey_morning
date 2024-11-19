package com.sf.honeymorning.account.authenticater.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import com.sf.honeymorning.account.authenticater.model.Token;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperty(
	Token accessToken,
	Token refreshToken,
	String issuer,
	String secretKey
) {
	@ConstructorBinding

	public JwtProperty(Token accessToken, Token refreshToken, String issuer, String secretKey) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.issuer = issuer;
		this.secretKey = secretKey;
	}
}
