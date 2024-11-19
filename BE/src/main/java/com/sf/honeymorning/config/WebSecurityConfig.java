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

import com.sf.honeymorning.account.authenticater.constant.CookieProperty;
import com.sf.honeymorning.account.authenticater.constant.JwtProperty;
import com.sf.honeymorning.account.authenticater.constant.SecurityUrlProperty;
import com.sf.honeymorning.account.authenticater.jwt.JwtAuthenticationFilter;
import com.sf.honeymorning.account.authenticater.jwt.JwtProviderManager;

@Configuration
@EnableConfigurationProperties({SecurityUrlProperty.class, JwtProperty.class, CookieProperty.class})
@EnableWebSecurity
public class WebSecurityConfig {

	private final JwtProviderManager jwtProviderManager;
	private final CookieProperty cookieProperty;
	private final SecurityUrlProperty securityUrlProperty;
	private final JwtProperty jwtProperty;

	public WebSecurityConfig(JwtProviderManager jwtProviderManager, CookieProperty cookieProperty,
		SecurityUrlProperty securityUrlProperty, JwtProperty jwtProperty) {
		this.jwtProviderManager = jwtProviderManager;
		this.cookieProperty = cookieProperty;
		this.securityUrlProperty = securityUrlProperty;
		this.jwtProperty = jwtProperty;
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
			.authorizeRequests()
			.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
			.requestMatchers(HttpMethod.GET, this.securityUrlProperty.urlPatternConfig().permitAll().get("GET"))
			.permitAll()
			.requestMatchers(HttpMethod.POST, this.securityUrlProperty.urlPatternConfig().permitAll().get("POST"))
			.permitAll()
			.requestMatchers(HttpMethod.PATCH, this.securityUrlProperty.urlPatternConfig().permitAll().get("PATCH"))
			.permitAll()
			.requestMatchers(HttpMethod.DELETE, this.securityUrlProperty.urlPatternConfig().permitAll().get("DELETE"))
			.permitAll()
			.requestMatchers(HttpMethod.PUT, this.securityUrlProperty.urlPatternConfig().permitAll().get("PUT"))
			.permitAll()
			.requestMatchers(HttpMethod.OPTIONS, this.securityUrlProperty.urlPatternConfig().permitAll().get("OPTIONS"))
			.permitAll()
			.anyRequest().authenticated()
			.and()
			.formLogin(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.headers(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.rememberMe(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.sessionManagement(AbstractHttpConfigurer::disable)
			.exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer.authenticationEntryPoint(
				authenticationEntryPoint()))
			.addFilterBefore(
				new JwtAuthenticationFilter(jwtProviderManager, jwtProperty, cookieProperty),
				UsernamePasswordAuthenticationFilter.class
			)
			// .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(new CorsConfiguration()))
			.build();

	}

	private String[] getIgnoringUrl(HttpMethod httpMethod) {
		return this.securityUrlProperty.urlPatternConfig().ignoring().get(httpMethod.name());
	}
}

