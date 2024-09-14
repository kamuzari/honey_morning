package com.sf.honeymorning.domain.alarm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@Tag(name = "알람")
@RequestMapping("/api/alarms")
@RestController
public class AlarmController {
	@Operation(
		summary = "설정 일부 수정"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "수정 성공",
			content = @Content(schema = @Schema(type = "string", example = "success"))
		)
	})
	@PatchMapping
	public ResponseEntity<String> update() {
		return ResponseEntity.ok("success");
	}

	@Operation(
		summary = "사용자 알람 조회"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "조회 성공",
			content = @Content(schema = @Schema(type = "string", example = "success"))
		)
	})
	@GetMapping
	public ResponseEntity<String> read() {
		return ResponseEntity.ok("success");
	}

	@Operation(
		summary = "사용자 알람 시작"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "알람 시작 성공",
			content = @Content(schema = @Schema(type = "string", example = "success"))
		)
	})
	@GetMapping("/start")
	public ResponseEntity<String> start() {
		return ResponseEntity.ok("success");
	}

}
