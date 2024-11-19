package com.sf.honeymorning.account.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.account.dto.request.AccountSignUpRequest;
import com.sf.honeymorning.account.dto.request.LoginAuthRequestDto;
import com.sf.honeymorning.account.dto.response.LoginAuthResponseDto;
import com.sf.honeymorning.account.dto.response.LogoutAuthResponseDto;
import com.sf.honeymorning.account.handler.LoginSuccessHandler;
import com.sf.honeymorning.account.handler.LogoutSuccessHandler;
import com.sf.honeymorning.account.service.AccountService;
import com.sf.honeymorning.account.authenticater.model.JwtAuthentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Tag(name = "계정")
@RequestMapping("/api/accounts")
@RestController
public class AccountController {

	private final AccountService accountService;
	private final LoginSuccessHandler loginSuccessHandler;
	private final LogoutSuccessHandler logoutSuccessHandler;

	public AccountController(AccountService accountService, LoginSuccessHandler loginSuccessHandler,
		LogoutSuccessHandler logoutSuccessHandler) {
		this.accountService = accountService;
		this.loginSuccessHandler = loginSuccessHandler;
		this.logoutSuccessHandler = logoutSuccessHandler;
	}

	@Operation(
		summary = "가입"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "회원 가입 성공"
		)
	})
	@PostMapping
	public void signUp(@Valid @RequestBody AccountSignUpRequest accountSignUpRequest) {
		accountService.create(accountSignUpRequest);
	}

	@Operation(
		summary = "이메일 중복 조회"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "이메일 중복 조회 성공",
			content = @Content(schema = @Schema(type = "string", example = "true or false"))
		)
	})
	@GetMapping("/check")
	public boolean emailCheck(@RequestParam("email") String email) {
		return accountService.validateEmail(email);
	}


	@Operation(summary = "인증", description = "인증에 성공하면 엑세스 토큰과 리프레시 토큰을 부여 받습니다.")
	@PostMapping("/login")
	public void login(
		@Valid
		@RequestBody
		LoginAuthRequestDto loginDto,

		HttpServletResponse response
	) {
		LoginAuthResponseDto loginResponse = accountService.login(loginDto);
		loginSuccessHandler.onLoginSuccess(response, loginResponse);
	}

	@Operation(summary = "로그아웃", description = "엑세스 토큰 유효시간을 0으로 변경하고 리프레시 토큰 정보를 삭제합니다.")
	@DeleteMapping("/logout")
	public void logout(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		HttpServletResponse response
	) {
		LogoutAuthResponseDto logoutResponse = accountService.logout(principal.id());
		logoutSuccessHandler.onLogoutSuccess(response, logoutResponse);
	}
}
