package com.honeymorning.api.context.mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeymorning.api.common.security.annotation.WithJwtMockUser;
import com.honeymorning.api.common.security.authentication.service.TokenService;

@WithJwtMockUser
public class MockControllerTest {
	protected static final String USERNAME = "kamusari";
	protected static final Long AUTH_ID = 1L;

	@Autowired
	protected MockMvc mockMvc;

	@SpyBean
	protected ObjectMapper objectMapper;

	@MockBean
	protected TokenService tokenService;

}
