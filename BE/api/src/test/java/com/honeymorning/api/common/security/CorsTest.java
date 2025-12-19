package com.honeymorning.api.common.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeymorning.api.common.security.authentication.JwtProviderManager;
import com.honeymorning.api.common.security.authentication.constant.JwtProperty;
import com.honeymorning.api.common.security.authentication.helper.JwtTokenGenerator;
import com.honeymorning.api.common.security.authentication.helper.JwtTokenWebExtractor;
import com.honeymorning.api.common.security.authentication.service.TokenService;
import com.honeymorning.api.config.WebSecurityConfig;
import com.honeymorning.api.user.adapter.in.web.handler.AuthenticateDiscardHandler;
import com.honeymorning.api.user.adapter.in.web.handler.AuthenticateSuccessHandler;

@WebMvcTest({
	WebSecurityConfig.class,
	JwtProviderManager.class,
	JwtTokenGenerator.class,
	JwtTokenWebExtractor.class,
	AuthenticateSuccessHandler.class,
	AuthenticateDiscardHandler.class,
	JwtProperty.class
})
public class CorsTest {
	static final String TEST_END_POINT = "/api/test";

	@MockitoSpyBean
	MockMvc mockMvc;

	@MockitoSpyBean
	ObjectMapper objectMapper;

	@MockitoBean
	TokenService tokenService;

	@DisplayName("지정한_ORIGIN일경우 시큐리티의 CORSFilter를 무사 통과한다")
	@ParameterizedTest(name = "origin : {0}")
	@ValueSource(strings = {"https://www.honeymorning.com", "http://localhost:3000"})
	void testPassFixedOrigin(String originUrl) throws Exception {
		mockMvc.perform(options(TEST_END_POINT)
				.header("Origin", originUrl)
				.header("Access-Control-Request-Method", "GET"))
			.andExpect(header().exists("Access-Control-Allow-Origin"))
			.andExpect(header().string("Access-Control-Allow-Origin", originUrl))
			.andExpect(header().exists("Access-Control-Allow-Methods"));
	}

	@DisplayName("지정한_ORIGIN이_아닐경우_시큐리티의_CORSFilter에서_403상태코드와_오류메시지를_반환한다")
	@ParameterizedTest(name = "invalidOrigin : {0}")
	@ValueSource(strings = {"http://www.honeymorning.com", "https://localhost:3000", "http://www.honey.com"})
	void failPassFixedOrigin(String invalidOriginUrl) throws Exception {
		mockMvc.perform(options("/api/test")
				.header("Origin", invalidOriginUrl)
				.header("Access-Control-Request-Method", "GET"))
			.andExpect(status().isForbidden())
			.andExpect(content().string("Invalid CORS request"));
	}
}
