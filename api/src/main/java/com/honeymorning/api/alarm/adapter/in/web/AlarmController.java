package com.honeymorning.api.alarm.adapter.in.web;

import java.time.LocalDateTime;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.honeymorning.api.alarm.adapter.in.web.dto.request.AlarmSetRequest;
import com.honeymorning.api.alarm.adapter.in.web.dto.response.AlarmResponse;
import com.honeymorning.api.alarm.adapter.in.web.dto.response.PreparedAlarmContentResponse;
import com.honeymorning.api.alarm.adapter.in.web.port.out.AlarmQueryPort;
import com.honeymorning.api.alarm.application.port.in.AlarmCommandUseCase;
import com.honeymorning.api.alarm.application.port.in.ValidateAlarmUseCase;
import com.honeymorning.api.common.security.core.JwtAuthentication;

import jakarta.validation.Valid;

@RequestMapping("/api/alarms")
@Validated
@RestController
public class AlarmController {

	private final AlarmQueryPort alarmQueryPort;
	private final AlarmCommandUseCase alarmCommandUseCase;
	private final ValidateAlarmUseCase validateAlarmUseCase;

	public AlarmController(
		AlarmQueryPort alarmQueryPort,
		AlarmCommandUseCase alarmCommandUseCase,
		ValidateAlarmUseCase validateAlarmUseCase) {

		this.alarmQueryPort = alarmQueryPort;
		this.alarmCommandUseCase = alarmCommandUseCase;
		this.validateAlarmUseCase = validateAlarmUseCase;
	}

	@PatchMapping
	public void set(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		@Valid @RequestBody
		AlarmSetRequest alarmRequestDto) {

		alarmCommandUseCase.update(alarmRequestDto, principal.id());
	}

	@GetMapping
	public AlarmResponse readMine(
		@AuthenticationPrincipal
		JwtAuthentication principal) {

		return alarmQueryPort.getMyAlarmWithMyTags(principal.id());
	}

	@GetMapping("/prepared")
	public PreparedAlarmContentResponse getPreparedAlarmContents(
		@AuthenticationPrincipal
		JwtAuthentication principal) {

		return alarmQueryPort.getPreparedAlarmContents(principal.id());
	}

	@GetMapping("/sleep")
	public void verifySleepMode(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		@RequestParam("startAt")
		LocalDateTime startAt) {

		validateAlarmUseCase.verifySleepMode(principal.id(), startAt);
	}
}
