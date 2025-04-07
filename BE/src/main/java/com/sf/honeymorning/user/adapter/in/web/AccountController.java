package com.sf.honeymorning.user.adapter.in.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.user.adapter.in.web.dto.request.AccountSignUpRequest;
import com.sf.honeymorning.user.adapter.in.web.dto.request.LoginAuthRequestDto;
import com.sf.honeymorning.user.adapter.in.web.dto.response.LogoutAuthResponseDto;
import com.sf.honeymorning.user.application.port.in.AuthenticateUseCase;
import com.sf.honeymorning.user.application.port.in.SignUpUseCase;
import com.sf.honeymorning.user.adapter.in.web.handler.AuthenticateSuccessHandler;
import com.sf.honeymorning.user.adapter.in.web.handler.AuthenticateDiscardHandler;
import com.sf.honeymorning.user.adapter.in.authentication.model.JwtAuthentication;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Tag(name = "계정")
@RequestMapping("/api/accounts")
@RestController
public class AccountController {
	private final SignUpUseCase signUpUseCase;
	private final AuthenticateUseCase authenticateUseCase;

	private final AuthenticateSuccessHandler authenticateSuccessHandler;
	private final AuthenticateDiscardHandler authenticateDiscardHandler;

	public AccountController(
		SignUpUseCase signUpUseCase, AuthenticateUseCase authenticateUseCase,
		AuthenticateSuccessHandler authenticateSuccessHandler,
		AuthenticateDiscardHandler authenticateDiscardHandler) {

		this.signUpUseCase = signUpUseCase;
		this.authenticateUseCase = authenticateUseCase;
		this.authenticateSuccessHandler = authenticateSuccessHandler;
		this.authenticateDiscardHandler = authenticateDiscardHandler;
	}

	@PostMapping
	public void signUp(
		@Valid
		@RequestBody
		AccountSignUpRequest accountSignUpRequest) {

		signUpUseCase.process(accountSignUpRequest);
	}

	@GetMapping("/check")
	public boolean emailCheck(@RequestParam("email") String email) {
		return signUpUseCase.isUsable(email);
	}

	@PostMapping("/login")
	public void login(
		@Valid
		@RequestBody
		LoginAuthRequestDto loginDto,

		HttpServletResponse response
	) {
		var loginResponse = authenticateUseCase.login(loginDto);
		authenticateSuccessHandler.onLoginSuccess(response, loginResponse);
	}

	@DeleteMapping("/logout")
	public void logout(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		HttpServletResponse response
	) {
		LogoutAuthResponseDto logoutResponse = authenticateUseCase.logout(principal.id());
		authenticateDiscardHandler.onLogoutSuccess(response, logoutResponse);
	}
}
