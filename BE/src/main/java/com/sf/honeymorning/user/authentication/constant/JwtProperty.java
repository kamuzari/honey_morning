package com.sf.honeymorning.user.authentication.constant;

import com.sf.honeymorning.user.authentication.model.Token;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

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
