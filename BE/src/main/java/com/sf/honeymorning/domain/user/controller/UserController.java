package com.sf.honeymorning.domain.user.controller;

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

@Tag(name = "회원")
@RequestMapping("/api/users")
@RestController
public class UserController {
	@Operation(
		summary = "회원가입"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "회원 가입 성공",
			content = @Content(schema = @Schema(type = "string", example = "success"))
		)
	})
	@PostMapping
	public ResponseEntity<String> register() {
		return ResponseEntity.ok("success");
	}

	@Operation(
		summary = "회원탈퇴"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "탈퇴 성공",
			content = @Content(schema = @Schema(type = "string", example = "success"))
		)
	})
	@DeleteMapping
	public ResponseEntity<String> withdraw() {
		return ResponseEntity.ok("success");
	}
}
