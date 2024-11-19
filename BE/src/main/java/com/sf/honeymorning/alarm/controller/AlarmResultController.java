package com.sf.honeymorning.alarm.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.account.authenticater.model.JwtAuthentication;
import com.sf.honeymorning.alarm.dto.AlarmResultDto;
import com.sf.honeymorning.domain.alarm.service.AlarmResultService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@RequestMapping("/api/alarms/result")
@RestController
public class AlarmResultController {

	AlarmResultService alarmResultService;

	@Operation(summary = "알람 결과 조회")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "알람 결과 조회 성공",
			content = @Content(schema = @Schema(type = "string", example = "success", implementation = AlarmResultDto.class))
		)
	})
	@GetMapping
	public ResponseEntity<List<AlarmResultDto>> getAlarmResult(
		@AuthenticationPrincipal
	JwtAuthentication principal) {
		List<AlarmResultDto> alarmResultDtoList = alarmResultService.findAlarmResult(principal.id());
		return new ResponseEntity<>(alarmResultDtoList, HttpStatus.OK);
	}

	// 알람 결과 저장
	@Operation(summary = "알람 결과 저장")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "알람 결과 저장 성공",
			content = @Content(schema = @Schema(type = "string", example = "success"))
		)
	})
	@PostMapping
	public ResponseEntity<String> addAlarmResult(
		@AuthenticationPrincipal
		JwtAuthentication principal,
		@RequestBody AlarmResultDto alarmResultDto) {
		alarmResultService.saveAlarmResult(principal.id(),alarmResultDto);
		return new ResponseEntity<>("알람 결과가 성공적으로 저장되었습니다.", HttpStatus.OK);
	}

	// 스트릭 일수 조회
	@Operation(
		summary = "연속 출석일수 확인")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "삭제 성공",
			content = @Content(schema = @Schema(type = "integer", example = "success", implementation = Integer.class))
		)
	})
	@GetMapping("/streak")
	public ResponseEntity<?> getStreak(
		@AuthenticationPrincipal
	JwtAuthentication principal) {
		int streak = alarmResultService.getStreak(principal.id());
		return new ResponseEntity<>(streak, HttpStatus.OK);
	}
}
