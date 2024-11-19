package com.sf.honeymorning.acount;

import static com.sf.honeymorning.user.entity.UserRole.ROLE_USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.honeymorning.account.authenticater.constant.JwtProperty;
import com.sf.honeymorning.account.authenticater.jwt.JwtProviderManager;
import com.sf.honeymorning.account.authenticater.service.TokenService;
import com.sf.honeymorning.account.controller.AccountController;
import com.sf.honeymorning.account.dto.request.LoginAuthRequestDto;
import com.sf.honeymorning.account.dto.response.LoginAuthResponseDto;
import com.sf.honeymorning.account.dto.response.LogoutAuthResponseDto;
import com.sf.honeymorning.account.dto.response.TokenResponseDto;
import com.sf.honeymorning.account.handler.LoginSuccessHandler;
import com.sf.honeymorning.account.handler.LogoutSuccessHandler;
import com.sf.honeymorning.account.service.AccountService;
import com.sf.honeymorning.config.WebSecurityConfig;
import com.sf.honeymorning.config.security.customSecurity.WithJwtMockUser;
import com.sf.honeymorning.user.entity.User;

@WebMvcTest({AccountController.class,
	WebSecurityConfig.class,
	JwtProviderManager.class,
	LoginSuccessHandler.class,
	LogoutSuccessHandler.class,
	JwtProperty.class})
public class AccountControllerTest {

	static final String URI_PREFIX = "/api/accounts";

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@MockBean
	protected TokenService tokenService;

	@Autowired
	JwtProperty jwtProperty;

	@Autowired
	LoginSuccessHandler loginSuccessHandler;

	@Autowired
	LogoutSuccessHandler logoutSuccessHandler;

	@MockBean
	AccountService accountService;

	@Test
	@DisplayName("로그인에 성공한다.")
	void testLogin() throws Exception {
		//given
		User existingUser = new User("whyWhale", "wls3123!", "gentle", ROLE_USER);
		var requestDto = new LoginAuthRequestDto(existingUser.getUsername(), existingUser.getPassword());

		var accessTokenResponse = new TokenResponseDto("access-token", "access-token", 1000);
		var refreshTokenResponse = new TokenResponseDto("refresh-token", "refresh-token", 60000);
		var loginResponse = new LoginAuthResponseDto(accessTokenResponse, refreshTokenResponse);

		given(accountService.login(requestDto)).willReturn(loginResponse);

		//when
		ResultActions perform = mockMvc.perform(
			post(URI_PREFIX + "/login")
				.content(objectMapper.writeValueAsString(requestDto))
				.contentType(APPLICATION_JSON)
		);

		//then
		verify(accountService, times(1)).login(requestDto);
		perform.andExpect(status().isOk())
			.andExpect(cookie().exists(loginResponse.accessToken().header()))
			.andExpect(
				cookie().maxAge(loginResponse.accessToken().header(),
					(int)accessTokenResponse.expirySeconds()))
			.andExpect(cookie().exists(loginResponse.refreshToken().header()))
			.andExpect(
				cookie().maxAge(loginResponse.refreshToken().header(),
					(int)refreshTokenResponse.expirySeconds())).andReturn();
	}

	@Test
	@WithJwtMockUser
	@DisplayName("인증된 사용자가 로그아웃 한다.")
	void testLogout() throws Exception {
		//given
		var logoutResponse = new LogoutAuthResponseDto(
			jwtProperty.accessToken().header(),
			jwtProperty.refreshToken().header());

		given(accountService.logout(any())).willReturn(logoutResponse);
		//when
		ResultActions perform = mockMvc.perform(
			delete(URI_PREFIX + "/logout")
		);
		//then
		perform.andExpect(status().isOk())
			.andExpect(cookie().exists(jwtProperty.accessToken().header()))
			.andExpect(cookie().maxAge(jwtProperty.accessToken().header(), 0))
			.andExpect(cookie().exists(jwtProperty.refreshToken().header()))
			.andExpect(cookie().maxAge(jwtProperty.refreshToken().header(), 0));
		verify(accountService, times(1)).logout(any());
	}

	@DisplayName("아이디 또는 비밀번호가 유효하지 않다면 실패한다.")
	@ParameterizedTest(name = "{index}: username: {0} | encodingPassword: {1}")
	@CsvSource(value = {
		"'',paswword",
		"'username',''",
		"'username','      '",
		"'       ','encodingPassword'"})
	void failNotProperArguments(String username, String password) throws Exception {
		//given
		LoginAuthRequestDto loginRequest = new LoginAuthRequestDto(username, password);
		String requestBody = objectMapper.writeValueAsString(loginRequest);
		//when
		ResultActions perform = mockMvc.perform(
			post(URI_PREFIX + "/login")
				.content(requestBody)
				.contentType(APPLICATION_JSON));
		//then
		perform.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("인증되지 않는 사용자가 로그아웃을 하면 실패한다.")
	void failLogoutWithNotAuthenticationUser() throws Exception {
		//given
		//when
		ResultActions perform = mockMvc.perform(delete(URI_PREFIX + "/logout"));
		//then
		perform.andExpect(status().isUnauthorized());
	}
}
