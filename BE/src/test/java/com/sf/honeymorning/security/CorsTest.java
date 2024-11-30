package com.sf.honeymorning.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.honeymorning.account.authenticater.constant.JwtProperty;
import com.sf.honeymorning.account.authenticater.jwt.JwtProviderManager;
import com.sf.honeymorning.account.authenticater.service.TokenService;
import com.sf.honeymorning.account.handler.LoginSuccessHandler;
import com.sf.honeymorning.account.handler.LogoutSuccessHandler;
import com.sf.honeymorning.config.WebSecurityConfig;

@WebMvcTest({
	WebSecurityConfig.class,
	JwtProviderManager.class,
	LoginSuccessHandler.class,
	LogoutSuccessHandler.class,
	JwtProperty.class
})
public class CorsTest {
	static final String TEST_END_POINT = "/api/test";

	@SpyBean
	MockMvc mockMvc;

	@SpyBean
	ObjectMapper objectMapper;

	@MockBean
	TokenService tokenService;

	@ParameterizedTest
	@ValueSource(strings = {"https://www.honeymorning.com", "http://localhost:3000"})
	void 지정한_ORIGIN일경우_시큐리티의_CORSFilter를_무사_통과한다(String originUrl) throws Exception {
		mockMvc.perform(options(TEST_END_POINT)
				.header("Origin", originUrl)
				.header("Access-Control-Request-Method", "GET"))
			.andExpect(header().exists("Access-Control-Allow-Origin"))
			.andExpect(header().string("Access-Control-Allow-Origin", originUrl))
			.andExpect(header().exists("Access-Control-Allow-Methods"));
	}

	@ParameterizedTest
	@ValueSource(strings = {"http://www.honeymorning.com", "https://localhost:3000", "http://www.honey.com"})
	void 지정한_ORIGIN이_아닐경우_시큐리티의_CORSFilter에서_403상태코드와_오류메시지를_반환한다(String invalidOriginUrl) throws Exception {
		mockMvc.perform(options("/api/test")
				.header("Origin", invalidOriginUrl)
				.header("Access-Control-Request-Method", "GET"))
			.andExpect(status().isForbidden())
			.andExpect(content().string("Invalid CORS request"));
	}
}
