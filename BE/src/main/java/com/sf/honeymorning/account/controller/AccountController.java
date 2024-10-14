package com.sf.honeymorning.account.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.account.dto.request.AccountSignUpRequest;
import com.sf.honeymorning.account.service.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "계정")
@RequestMapping("/api/account")
@RestController
public class AccountController {

	private final AccountService accountService;

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
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

}
