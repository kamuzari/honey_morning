package com.sf.honeymorning.domain.user.controller;

import com.sf.honeymorning.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "회원")
@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;

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

	@Operation(
			summary = "이메일 중복 조회"
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "이메일 중복 조회 성공",
					content = @Content(schema = @Schema(type = "string", example = "success"))
			)
	})
	@GetMapping("/check/email")
	public ResponseEntity<?> emailCheck(@RequestParam("email") String email) {
		boolean isDuplicated = userService.validateEmail(email);
		return new ResponseEntity<>(isDuplicated, HttpStatus.OK);
	}
}
