package com.sf.honeymorning.domain.alarm.controller;

import com.sf.honeymorning.domain.alarm.dto.AlarmRequestDto;
import com.sf.honeymorning.domain.alarm.dto.AlarmResponseDto;
import com.sf.honeymorning.domain.alarm.service.AlarmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "알람")
@RequestMapping("/api/alarms")
@RestController
public class AlarmController {
	private final AlarmService alarmService;

	public AlarmController(AlarmService alarmService) {
		this.alarmService = alarmService;
	}

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
	public ResponseEntity<?> update(@RequestBody AlarmRequestDto alarmRequestDto) {
		return alarmService.updateAlarm(alarmRequestDto);
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
	public ResponseEntity<?> read() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		// 사용자가 알람 설정창에 들어갈 때 알람 정보 조회
		AlarmResponseDto alarm = alarmService.findAlarmByUsername(username);
		
		return ResponseEntity.ok(alarm);
	}

//	@Operation(
//		summary = "사용자 알람 시작"
//	)
//	@ApiResponses(value = {
//		@ApiResponse(
//			responseCode = "200",
//			description = "알람 시작 성공",
//			content = @Content(schema = @Schema(type = "string", example = "success"))
//		)
//	})
//	@GetMapping("/start")
//	public ResponseEntity<String> start() {
//		return ResponseEntity.ok("success");
//	}

}
