package com.sf.honeymorning.context.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.sf.honeymorning.security.weaving.WithJwtMockUser;
import com.sf.honeymorning.user.adapter.in.authentication.service.TokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

@WithJwtMockUser
public class MockControllerTest {
    protected static final Faker DATE_GENERATOR = new Faker();
    protected static final String USERNAME = "kamusari";
    protected static final Long AUTH_ID = 1L;

    @Autowired
    protected MockMvc mockMvc;

    @SpyBean
    protected ObjectMapper objectMapper;

    @MockBean
    protected TokenService tokenService;

}
