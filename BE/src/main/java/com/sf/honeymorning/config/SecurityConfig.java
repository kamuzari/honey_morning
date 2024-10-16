package com.sf.honeymorning.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.sf.honeymorning.authentication.filter.CustomLogoutFilter;
import com.sf.honeymorning.authentication.filter.JWTFilter;
import com.sf.honeymorning.authentication.filter.LoginFilter;
import com.sf.honeymorning.authentication.util.JWTUtil;

@Configuration
public class SecurityConfig {

	private final AuthenticationConfiguration authenticationConfiguration;
	private final JWTUtil jwtUtil;

	public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {
		this.authenticationConfiguration = authenticationConfiguration;
		this.jwtUtil = jwtUtil;
	}

	@Value("${cors.allowedOrigins.frontend}")
	private String allowedOrigins;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
		throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
			.httpBasic(Customizer.withDefaults())
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api/auth/**", "/api/users/**").permitAll()
				.anyRequest().authenticated()
			)
			.formLogin(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.rememberMe(AbstractHttpConfigurer::disable)
			.sessionManagement(AbstractHttpConfigurer::disable)
			.cors(httpSecurityCorsConfigurer -> {
				CorsConfiguration configuration = new CorsConfiguration();

				// 허용할 포트
				configuration.setAllowedOrigins(Collections.singletonList(allowedOrigins));
				configuration.addAllowedOriginPattern("http://localhost:5173");
				// 허용할 메서드
				configuration.setAllowedMethods(Collections.singletonList("*"));
				// 요청이 오는 곳의 credential을 true로 변경
				configuration.setAllowCredentials(true);

				configuration.setAllowedHeaders(Collections.singletonList("*"));
				configuration.setMaxAge(3600L);

				// 응답시 Authorization을 같이 보내준다.
				configuration.setExposedHeaders(Collections.singletonList("access"));

			}).addFilterBefore(new JWTFilter(jwtUtil),
				UsernamePasswordAuthenticationFilter.class)
			.addFilterAt(
				new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil),
				UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new CustomLogoutFilter(jwtUtil), LogoutFilter.class)
			.build();
	}

}
