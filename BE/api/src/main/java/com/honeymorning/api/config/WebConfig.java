package com.honeymorning.api.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.honeymorning.api.config.constant.WebCorsProperties;

@EnableConfigurationProperties(WebCorsProperties.class)
@Configuration
public class WebConfig implements WebMvcConfigurer {
	private static final String PATH_PATTERN = "/**";

	private final WebCorsProperties webCorsProperties;

	public WebConfig(WebCorsProperties webCorsProperties) {
		this.webCorsProperties = webCorsProperties;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping(PATH_PATTERN)
			.allowedOrigins(webCorsProperties.allowedOrigins().toArray(String[]::new))
			.allowedMethods(webCorsProperties.allowedMethods().toArray(String[]::new))
			.allowedHeaders(webCorsProperties.allowedHeaders().toArray(String[]::new))
			.exposedHeaders(webCorsProperties.allowedHeaders().toArray(String[]::new))
			.allowCredentials(webCorsProperties.allowCredentials());
	}

}
