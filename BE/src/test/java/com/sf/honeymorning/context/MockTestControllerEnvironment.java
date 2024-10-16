package com.sf.honeymorning.context;

import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.sf.honeymorning.authentication.util.JWTUtil;
import com.sf.honeymorning.config.SecurityConfig;
import com.sf.honeymorning.config.security.WithMockCustomUser;

@WithMockCustomUser(username = "kamusari")
@Import({SecurityConfig.class, JWTUtil.class})
public class MockTestControllerEnvironment {
	protected static final Faker FAKER = new Faker();
	protected static final String USERNAME = "kamusari";

	@SpyBean
	protected MockMvc mockMvc;

	@SpyBean
	protected ObjectMapper objectMapper;

}
