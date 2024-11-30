package com.sf.honeymorning.context;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.sf.honeymorning.account.authenticater.service.TokenService;
import com.sf.honeymorning.security.WithJwtMockUser;

@WithJwtMockUser
public class MockTestControllerEnvironment {
	protected static final Faker FAKER = new Faker();
	protected static final String USERNAME = "kamusari";

	@SpyBean
	protected MockMvc mockMvc;

	@SpyBean
	protected ObjectMapper objectMapper;

	@MockBean
	protected TokenService tokenService;

}
