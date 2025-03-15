package com.sf.honeymorning.alarm.controller;

import java.time.LocalDateTime;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.alarm.controller.dto.request.AlarmSetRequest;
import com.sf.honeymorning.alarm.controller.dto.response.AlarmResponse;
import com.sf.honeymorning.alarm.controller.dto.response.PreparedAlarmContentResponse;
import com.sf.honeymorning.alarm.service.AlarmContentService;
import com.sf.honeymorning.alarm.service.AlarmService;
import com.sf.honeymorning.user.authentication.model.JwtAuthentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RequestMapping("/api/alarms")
@Validated
@RestController
public class AlarmController {

	private final AlarmService alarmService;
	private final AlarmContentService alarmContentService;

	public AlarmController(AlarmService alarmService, AlarmContentService alarmContentService) {
		this.alarmService = alarmService;
		this.alarmContentService = alarmContentService;
	}

	@PatchMapping
	public void set(@AuthenticationPrincipal JwtAuthentication principal,
		@Valid @RequestBody AlarmSetRequest alarmRequestDto) {

		alarmService.set(alarmRequestDto, principal.id());
	}

	@GetMapping
	public AlarmResponse readMine(@AuthenticationPrincipal JwtAuthentication principal) {
		return alarmService.getMyAlarmWithMyTags(principal.id());
	}

	@GetMapping("/prepared")
	public PreparedAlarmContentResponse getPreparedAlarmContents(@AuthenticationPrincipal JwtAuthentication principal) {
		return alarmContentService.getPreparedAlarmContents(principal.id());
	}

	@GetMapping("/sleep")
	public void verifySleepMode(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		@RequestParam("startAt")
		LocalDateTime startAt
	) {
		alarmService.verifySleepMode(principal.id(), startAt);
	}
}
