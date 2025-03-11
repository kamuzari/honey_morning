package com.sf.honeymorning.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.sf.honeymorning.security.WithJwtMockUser;
import com.sf.honeymorning.user.authentication.service.TokenService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

@WithJwtMockUser
public class MockTestControllerEnvironment {
    protected static final Faker FAKER = new Faker();
    protected static final String USERNAME = "kamusari";
    protected static final Long AUTH_ID = 1L;

    @SpyBean
    protected MockMvc mockMvc;

    @SpyBean
    protected ObjectMapper objectMapper;

    @MockBean
    protected TokenService tokenService;

}
