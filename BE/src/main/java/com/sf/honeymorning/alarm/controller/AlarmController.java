package com.sf.honeymorning.alarm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.alarm.dto.request.AlarmSetRequest;
import com.sf.honeymorning.alarm.dto.response.AlarmResponse;
import com.sf.honeymorning.alarm.service.AlarmService;
import com.sf.honeymorning.domain.alarm.dto.AlarmStartDto;
import com.sf.honeymorning.user.dto.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "알람")
@RequestMapping("/api/alarms")
@RestController
public class AlarmController {

	private final AlarmService alarmService;

	public AlarmController(AlarmService alarmService) {
		this.alarmService = alarmService;
	}

	@Operation(
		summary = "알람 설정 일부 수정"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "수정 성공",
			content = @Content(schema = @Schema(implementation = Void.class))
		)
	})
	@PatchMapping
	public void set(
		@AuthenticationPrincipal CustomUserDetails user
		, @Valid @RequestBody AlarmSetRequest alarmRequestDto) {

		System.out.println("user = " + user);
		alarmService.set(alarmRequestDto, user.getUsername());
	}

	@Operation(
		summary = "나의 알람 조회"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "조회 성공",
			content = @Content(schema = @Schema(implementation = AlarmResponse.class))
		)
	})
	@GetMapping
	public AlarmResponse read(@AuthenticationPrincipal CustomUserDetails user) {
		return alarmService.getMyAlarm(user.getUsername());
	}

	@Operation(
		summary = "사용자 알람 시작"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "알람 시작 성공",
			content = @Content(schema = @Schema(implementation = AlarmStartDto.class))
		)
	})
	@PostMapping("/start")
	public ResponseEntity<AlarmStartDto> start() {
		AlarmStartDto alarmStartDto = alarmService.getThings();
		return ResponseEntity.ok(alarmStartDto);
	}

	@Operation(
		summary = "수면 시작"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "알람 시작 성공"
		)
	})
	@GetMapping("/sleep")
	public ResponseEntity<?> sleep() {
		alarmService.getSleep();
		return ResponseEntity.ok(null);
	}
}
