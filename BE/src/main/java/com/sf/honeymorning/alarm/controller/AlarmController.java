package com.sf.honeymorning.alarm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.account.authenticater.model.JwtAuthentication;
import com.sf.honeymorning.alarm.controller.dto.response.PreparedAlarmContentResponse;
import com.sf.honeymorning.alarm.dto.request.AlarmSetRequest;
import com.sf.honeymorning.alarm.dto.response.AlarmResponse;
import com.sf.honeymorning.alarm.service.AlarmService;
import com.sf.honeymorning.alarm.service.PreparedAlarmContentService;
import com.sf.honeymorning.domain.alarm.dto.AlarmStartDto;

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
	private final PreparedAlarmContentService preparedAlarmContentService;

	public AlarmController(AlarmService alarmService, PreparedAlarmContentService preparedAlarmContentService) {
		this.alarmService = alarmService;
		this.preparedAlarmContentService = preparedAlarmContentService;
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
	public void set(@AuthenticationPrincipal JwtAuthentication principal,
		@Valid @RequestBody AlarmSetRequest alarmRequestDto) {

		alarmService.set(alarmRequestDto, principal.id());
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
	public AlarmResponse readMine(@AuthenticationPrincipal JwtAuthentication principal) {
		return alarmService.getMyAlarm(principal.id());
	}

	@Operation(
		summary = "알람 시작 전에 준비된 알람 콘텐츠들을 모두 가져옵니다."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "준비된 콘텐츠 전달 성공",
			content = @Content(schema = @Schema(implementation = AlarmStartDto.class))
		)
	})
	@GetMapping("/prepared")
	public PreparedAlarmContentResponse getPreparedAlarmContents(@AuthenticationPrincipal JwtAuthentication principal) {
		return preparedAlarmContentService.getPreparedAlarmContents(principal.id());
	}

	@Operation(
		summary = "수면 모드 확인"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "알람 시작 성공"
		)
	})
	@GetMapping("/sleep")
	public ResponseEntity<?> canSleepMode(
		@AuthenticationPrincipal
		JwtAuthentication principal
	) {
		alarmService.getSleep(principal.id());
		return ResponseEntity.ok(null);
	}
}
