package com.sf.honeymorning.alarm.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.account.authenticater.model.JwtAuthentication;
import com.sf.honeymorning.alarm.controller.dto.request.AlarmResultRequestCreateDto;
import com.sf.honeymorning.alarm.controller.dto.response.AlarmResultResponseDto;
import com.sf.honeymorning.alarm.service.AlarmResultService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Validated
@RequestMapping("/api/alarm-results")
@RestController
public class AlarmResultController {

	AlarmResultService alarmResultService;

	public AlarmResultController(AlarmResultService alarmResultService) {
		this.alarmResultService = alarmResultService;
	}

	@Operation(summary = "알람 결과 커서 페이징 조회")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "알람 결과 조회 성공",
			content = @Content(schema = @Schema(implementation = AlarmResultResponseDto.class))
		)
	})
	@GetMapping
	public List<AlarmResultResponseDto> getAlarmResult(
		@AuthenticationPrincipal JwtAuthentication principal,
		@RequestParam(required = false, value = "lastId", defaultValue = "0") Long lastId) {

		return alarmResultService.getContents(principal.id(), lastId);
	}

	@Operation(summary = "알람 콘텐츠 수행 후 퀴즈 맟춘 갯수, 참석 여부 결과 저장")
	@PostMapping
	public void addAlarmResult(
		@AuthenticationPrincipal JwtAuthentication principal,
		@RequestBody AlarmResultRequestCreateDto alarmResultResponseDto) {

		alarmResultService.add(principal.id(), alarmResultResponseDto);
	}

	@Operation(
		summary = "연속 출석에 대한 최대 스트릭 가져오기")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			content = @Content(schema = @Schema(type = "integer", example = "success", implementation = Integer.class))
		)
	})
	@GetMapping("/streak")
	public ResponseEntity<?> getStreak(
		@AuthenticationPrincipal
		JwtAuthentication principal) {
		int streak = alarmResultService.getMaximumStreak(principal.id());
		return new ResponseEntity<>(streak, HttpStatus.OK);
	}
}
