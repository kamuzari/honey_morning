package com.sf.honeymorning.account.authenticater.constant;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "security")
public record SecurityUrlProperty(UrlPattern urlPattern) {
	@ConstructorBinding
	public SecurityUrlProperty {
	}

	public record UrlPattern(Map<String, String[]> ignoring, Map<String, String[]> permitAll) {
		@ConstructorBinding
		public UrlPattern {
		}
	}
}
