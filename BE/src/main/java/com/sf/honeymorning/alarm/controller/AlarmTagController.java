package com.sf.honeymorning.alarm.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.account.authenticater.model.JwtAuthentication;
import com.sf.honeymorning.alarm.dto.AlarmTagResponseDto;
import com.sf.honeymorning.alarm.service.AlarmTagService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "알람태그")
@RequestMapping("/api/alarm-tags")
@RestController
public class AlarmTagController {

	private final AlarmTagService alarmTagService;

	public AlarmTagController(AlarmTagService alarmTagService) {
		this.alarmTagService = alarmTagService;
	}

	@Operation(
		summary = "알람 설정에서의 나의 카테고리 조회"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "조회 성공",
			content = @Content(schema = @Schema(implementation = AlarmTagResponseDto.class))
		)
	})
	@GetMapping
	public List<AlarmTagResponseDto> getMyTags(@AuthenticationPrincipal JwtAuthentication principal) {
		return alarmTagService.getAlarmTags(principal.id());
	}

	@Operation(
		summary = "알람 카테고리 추가")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "저장 성공",
			content = @Content(schema = @Schema(type = "string", example = "success", implementation = AlarmTagResponseDto.class))
		)
	})
	@PostMapping
	public ResponseEntity<String> saveAlarmCategory(
		@AuthenticationPrincipal
		JwtAuthentication principal,
		@RequestBody String word) {
		alarmTagService.addAlarmCategory(principal.id(), word);
		return new ResponseEntity<>("알람 카테고리를 성공적으로 추가하였습니다.", HttpStatus.OK);
	}

	@Operation(
		summary = "카테고리 문자를 통한 알람 카테고리 삭제")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "삭제 성공",
			content = @Content(schema = @Schema(type = "string", example = "success", implementation = AlarmTagResponseDto.class))
		)
	})
	@DeleteMapping
	public ResponseEntity<?> removeAlarmCategory(
		@AuthenticationPrincipal
		JwtAuthentication principal
		, @RequestBody String word) {
		alarmTagService.deleteAlarmCategory(principal.id(), word);
		return ResponseEntity.ok("alarm category successfully deleted");
	}

	@Operation(
		summary = "카테고리 문자를 통한 알람 카테고리 수정")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "수정 성공",
			content = @Content(schema = @Schema(type = "string", example = "success", implementation = AlarmTagResponseDto.class))
		)
	})
	@PatchMapping
	public ResponseEntity<?> patchAlarmCategory(
		@AuthenticationPrincipal
		JwtAuthentication principal,
		@RequestBody Map<String, List<String>> request) {
		List<String> tagWords = request.get("categoryWords");
		alarmTagService.patchAlarmCategory(principal.id(), tagWords);
		return ResponseEntity.ok("alarm category successfully updated");
	}
}
