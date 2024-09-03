package com.sf.honeymorning.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {
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
						.url("https://www.honey-morining.com")
				)
			);
	}
}

