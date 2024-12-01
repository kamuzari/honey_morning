package com.sf.honeymorning.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.sf.honeymorning.account.authenticater.constant.CookieProperty;
import com.sf.honeymorning.account.authenticater.constant.JwtProperty;
import com.sf.honeymorning.account.authenticater.constant.SecurityUrlProperty;
import com.sf.honeymorning.account.authenticater.jwt.JwtAuthenticationFilter;
import com.sf.honeymorning.account.authenticater.jwt.JwtProviderManager;
import com.sf.honeymorning.config.constant.WebCorsProperties;

@Configuration
@EnableConfigurationProperties({SecurityUrlProperty.class,
	JwtProperty.class,
	CookieProperty.class,
	WebCorsProperties.class})
@EnableWebSecurity
public class WebSecurityConfig {

	private final JwtProviderManager jwtProviderManager;
	private final CookieProperty cookieProperty;
	private final SecurityUrlProperty securityUrlProperty;
	private final JwtProperty jwtProperty;
	private final WebCorsProperties webCorsProperties;

	public WebSecurityConfig(JwtProviderManager jwtProviderManager,
		CookieProperty cookieProperty,
		SecurityUrlProperty securityUrlProperty,
		JwtProperty jwtProperty,
		WebCorsProperties webCorsProperties) {
		this.jwtProviderManager = jwtProviderManager;
		this.cookieProperty = cookieProperty;
		this.securityUrlProperty = securityUrlProperty;
		this.jwtProperty = jwtProperty;
		this.webCorsProperties = webCorsProperties;
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.requestMatchers(HttpMethod.GET, this.getIgnoringUrl(HttpMethod.GET))
			.requestMatchers(HttpMethod.POST, this.getIgnoringUrl(HttpMethod.POST))
			.requestMatchers(HttpMethod.PATCH, this.getIgnoringUrl(HttpMethod.PATCH))
			.requestMatchers(HttpMethod.DELETE, this.getIgnoringUrl(HttpMethod.PUT))
			.requestMatchers(HttpMethod.PUT, this.getIgnoringUrl(HttpMethod.DELETE))
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}

	@Bean
	AuthenticationEntryPoint authenticationEntryPoint() {
		return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
			.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
				authorizationManagerRequestMatcherRegistry.requestMatchers(CorsUtils::isPreFlightRequest)
					.permitAll()
					.requestMatchers(HttpMethod.GET, this.securityUrlProperty.urlPattern().permitAll().get("GET"))
					.permitAll()
					.requestMatchers(HttpMethod.POST,
						this.securityUrlProperty.urlPattern().permitAll().get("POST"))
					.permitAll()
					.requestMatchers(HttpMethod.PATCH,
						this.securityUrlProperty.urlPattern().permitAll().get("PATCH"))
					.permitAll()
					.requestMatchers(HttpMethod.DELETE,
						this.securityUrlProperty.urlPattern().permitAll().get("DELETE"))
					.permitAll()
					.requestMatchers(HttpMethod.PUT, this.securityUrlProperty.urlPattern().permitAll().get("PUT"))
					.permitAll()
					.requestMatchers(HttpMethod.OPTIONS,
						this.securityUrlProperty.urlPattern().permitAll().get("OPTIONS"))
					.permitAll()
					.anyRequest()
					.authenticated()
			)
			.formLogin(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.headers(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.rememberMe(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.sessionManagement(AbstractHttpConfigurer::disable)
			.exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer.authenticationEntryPoint(authenticationEntryPoint()))
			.addFilterBefore(
				new JwtAuthenticationFilter(jwtProviderManager, jwtProperty, cookieProperty),
				UsernamePasswordAuthenticationFilter.class
			)
			.cors(
				httpSecurityCorsConfigurer -> {
					CorsConfiguration configuration = new CorsConfiguration();
					configuration.setAllowedOrigins(webCorsProperties.allowedOrigins());
					configuration.setAllowedMethods(webCorsProperties.allowedMethods());
					configuration.setAllowedHeaders(webCorsProperties.allowedHeaders());
					configuration.setExposedHeaders(webCorsProperties.exposedHeaders());
					configuration.setAllowCredentials(webCorsProperties.allowCredentials());

					UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
					source.registerCorsConfiguration("/**", configuration);

					httpSecurityCorsConfigurer.configurationSource(source);
				})
			.build();

	}

	private String[] getIgnoringUrl(HttpMethod httpMethod) {
		return this.securityUrlProperty.urlPattern().ignoring().get(httpMethod.name());
	}
}

