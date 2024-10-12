package com.sf.honeymorning.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Profile("local")
@Configuration
public class LocalSwaggerConfig {
	@Value("${spring.application.name}")
	String applicationName;

	@Bean
	public OpenAPI springShopOpenAPI() {
		return new OpenAPI()
			.info(new Info().title(applicationName + " API 명세서")
				.version("0.1")
				.license(
					new License()
						.name(applicationName)
						.url("https://www.honeymorning.store")
				))
			.addSecurityItem(new SecurityRequirement().addList("JWT"))
			.components(new Components()
				.addSecuritySchemes("JWT",
					new SecurityScheme()
						.type(SecurityScheme.Type.APIKEY)
						.in(SecurityScheme.In.HEADER)
						.name("access")
						.scheme("Bearer")
						.description("access token 을 저장하고 API 요청을 하면 인증된 사용자 요청으로 인식합니다.")));
	}
}
