package com.sf.honeymorning.alarm.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.account.authenticater.model.JwtAuthentication;
import com.sf.honeymorning.alarm.controller.dto.response.AlarmTagResponseDto;
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

	@Operation(summary = "알람 설정에서의 나의 카테고리 조회")
	@ApiResponses(value = {
		@ApiResponse(content = @Content(schema = @Schema(implementation = AlarmTagResponseDto.class)))
	})
	@GetMapping
	public List<AlarmTagResponseDto> getMyTags(@AuthenticationPrincipal JwtAuthentication principal) {
		return alarmTagService.getMyAlarmTags(principal.id());
	}

	@Operation(summary = "알람 카테고리 추가")
	@PostMapping
	public void add(@AuthenticationPrincipal JwtAuthentication principal, @RequestBody String word
	) {
		alarmTagService.addAlarmCategory(principal.id(), word);
	}

	@Operation(summary = "알람 카테고리 삭제")
	@DeleteMapping
	public void remove(@AuthenticationPrincipal JwtAuthentication principal, @RequestBody String word) {
		alarmTagService.deleteAlarmCategory(principal.id(), word);
	}
}
