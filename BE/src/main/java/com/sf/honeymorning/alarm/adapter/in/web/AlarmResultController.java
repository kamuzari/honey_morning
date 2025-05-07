package com.sf.honeymorning.alarm.adapter.in.web;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.alarm.adapter.in.web.dto.request.AddAlarmResultRequestDto;
import com.sf.honeymorning.alarm.adapter.in.web.dto.response.AlarmResultResponseDto;
import com.sf.honeymorning.alarm.adapter.in.web.port.out.AlarmResultQueryPort;
import com.sf.honeymorning.alarm.application.port.in.AlarmResultCommandUseCase;
import com.sf.honeymorning.user.adapter.in.authentication.model.JwtAuthentication;

import jakarta.validation.Valid;

@Validated
@RequestMapping("/api/alarm-results")
@RestController
public class AlarmResultController {
	private final AlarmResultQueryPort alarmResultQueryPort;
	private final AlarmResultCommandUseCase alarmResultCommandUseCase;

	public AlarmResultController(AlarmResultQueryPort alarmResultQueryPort,
		AlarmResultCommandUseCase alarmResultCommandUseCase) {
		this.alarmResultQueryPort = alarmResultQueryPort;
		this.alarmResultCommandUseCase = alarmResultCommandUseCase;
	}

	@GetMapping
	public List<AlarmResultResponseDto> getAlarmResults(
		@AuthenticationPrincipal JwtAuthentication principal,
		@RequestParam(required = false, value = "lastId", defaultValue = "0") Long lastId) {

		return alarmResultQueryPort.getMyAlarmResults(principal.id(), lastId);
	}

	@PostMapping
	public void add(
		@AuthenticationPrincipal JwtAuthentication principal,
		@Valid @RequestBody AddAlarmResultRequestDto alarmResultResponseDto) {

		alarmResultCommandUseCase.add(principal.id(), alarmResultResponseDto);
	}

	@GetMapping("/streak")
	public int getMaximumStreak(
		@AuthenticationPrincipal
		JwtAuthentication principal) {

		return alarmResultQueryPort.getMaximumStreak(principal.id());
	}
}
