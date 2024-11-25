package com.sf.honeymorning.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.sf.honeymorning.config.constant.WebCorsProperties;

@EnableConfigurationProperties(WebCorsProperties.class)
@Configuration
public class WebConfig implements WebMvcConfigurer {
	private final WebCorsProperties webCorsProperties;

	public WebConfig(WebCorsProperties webCorsProperties) {
		this.webCorsProperties = webCorsProperties;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("http://localhost:5173")
			.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
			.allowedHeaders("Set-Cookie")
			.exposedHeaders("Set-Cookie")
			.allowCredentials(true);
	}

}
