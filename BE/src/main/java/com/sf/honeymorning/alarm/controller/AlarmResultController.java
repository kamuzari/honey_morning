package com.sf.honeymorning.alarm.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.alarm.controller.dto.request.AddAlarmResultRequestDto;
import com.sf.honeymorning.alarm.controller.dto.response.AlarmResultResponseDto;
import com.sf.honeymorning.alarm.service.AlarmResultService;
import com.sf.honeymorning.user.adapter.in.authentication.model.JwtAuthentication;

import jakarta.validation.Valid;

@Validated
@RequestMapping("/api/alarm-results")
@RestController
public class AlarmResultController {

	AlarmResultService alarmResultService;

	public AlarmResultController(AlarmResultService alarmResultService) {
		this.alarmResultService = alarmResultService;
	}

	@GetMapping
	public List<AlarmResultResponseDto> getAlarmResults(
		@AuthenticationPrincipal JwtAuthentication principal,
		@RequestParam(required = false, value = "lastId", defaultValue = "0") Long lastId) {

		return alarmResultService.getPageContent(principal.id(), lastId);
	}

	@PostMapping
	public void add(
		@AuthenticationPrincipal JwtAuthentication principal,
		@Valid @RequestBody AddAlarmResultRequestDto alarmResultResponseDto) {

		alarmResultService.add(principal.id(), alarmResultResponseDto);
	}

	@GetMapping("/streak")
	public int getMaximumStreak(
		@AuthenticationPrincipal
		JwtAuthentication principal) {

		return alarmResultService.getMaximumStreak(principal.id());
	}
}
