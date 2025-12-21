package com.honeymorning.api.context.mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
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

	@MockitoSpyBean
	protected ObjectMapper objectMapper;

	@MockitoBean
	protected TokenService tokenService;

}
