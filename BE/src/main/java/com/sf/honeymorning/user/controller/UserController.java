package com.sf.honeymorning.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.alarm.service.AlarmService;
import com.sf.honeymorning.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "회원")
@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final AlarmService alarmService;

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
	@GetMapping("/check/email")
	public ResponseEntity<?> emailCheck(@RequestParam("email") String email) {
		boolean isDuplicated = userService.validateEmail(email);
		return new ResponseEntity<>(isDuplicated, HttpStatus.OK);
	}
}
