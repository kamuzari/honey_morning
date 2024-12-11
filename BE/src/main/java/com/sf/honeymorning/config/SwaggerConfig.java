package com.sf.honeymorning.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.sf.honeymorning.account.authenticater.constant.JwtProperty;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Profile({"local", "production"})
@Configuration
public class SwaggerConfig {

	private final JwtProperty jwtProperty;
	@Value("${spring.application.name}")
	String applicationName;

	public SwaggerConfig(JwtProperty jwtProperty) {
		this.jwtProperty = jwtProperty;
	}

	@Bean
	public OpenAPI springShopOpenAPI() {
		return new OpenAPI()
			.info(createLicense())
			.servers(createAvailableServers())
			.addSecurityItem(createItem())
			.components(createComponents()
			);
	}

	private Info createLicense() {
		return new Info().title(applicationName + " API 명세서")
			.version("0.1")
			.license(
				new License()
					.name(applicationName)
					.url("https://www.honeymorning.store")
			);
	}

	private List<Server> createAvailableServers() {
		return List.of(
			new Server()
				.url("http://localhost:8080")
				.description("Local 서버"),
			new Server()
				.url("https://www.honeymorning.store")
				.description("Production 서버")
		);
	}

	private Components createComponents() {
		return new Components()
			.addSecuritySchemes(jwtProperty.accessToken().header(), new SecurityScheme()
				.type(SecurityScheme.Type.APIKEY)
				.in(SecurityScheme.In.COOKIE)
				.name(jwtProperty.accessToken().header())
				.description("Set-Cookie로 전달된 access token을 사용합니다."))

			.addSecuritySchemes(jwtProperty.refreshToken().header(), new SecurityScheme()
				.type(SecurityScheme.Type.APIKEY)
				.in(SecurityScheme.In.COOKIE)
				.name(jwtProperty.refreshToken().header())
				.description("Set-Cookie로 전달된 refresh token을 사용합니다."));
	}

	private SecurityRequirement createItem() {
		return new SecurityRequirement()
			.addList(jwtProperty.accessToken().header())
			.addList(jwtProperty.refreshToken().header());
	}
}
