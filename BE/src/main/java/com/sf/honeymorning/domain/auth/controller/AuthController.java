package com.sf.honeymorning.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "인증")
@RequestMapping("/api/auth")
@RestController
public class AuthController {

	@Operation(
		summary = "로그인"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "로그인 성공",
			content = @Content(schema = @Schema(type = "string", example = "success"))
		)
	})
	@PostMapping("/login")
	public ResponseEntity<String> login() {
		return ResponseEntity.ok("success");
	}

	@Operation(
		summary = "로그아웃"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "로그아웃 성공",
			content = @Content(schema = @Schema(type = "string", example = "success"))
		)
	})
	@PostMapping("/logout")
	public ResponseEntity<String> logout() {
		return ResponseEntity.ok("success");
	}
}
