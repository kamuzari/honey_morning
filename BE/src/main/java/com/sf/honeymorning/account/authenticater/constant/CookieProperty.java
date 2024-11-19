package com.sf.honeymorning.account.authenticater.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.boot.web.server.Cookie;

@ConfigurationProperties(prefix = "cookie")
public record CookieProperty(
	Boolean secure,
	Cookie.SameSite sameSite,
	String domain) {
	@ConstructorBinding
	public CookieProperty {
	}
}
