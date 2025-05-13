package com.sf.honeymorning.alarm.adapter.in.web;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.alarm.adapter.in.web.dto.request.AddAlarmTagRequestDto;
import com.sf.honeymorning.alarm.adapter.in.web.dto.response.AlarmTagResponseDto;
import com.sf.honeymorning.alarm.adapter.in.web.port.out.AlarmTagQueryPort;
import com.sf.honeymorning.alarm.application.port.in.AlarmTagCommandUseCase;
import com.sf.honeymorning.user.adapter.in.authentication.model.JwtAuthentication;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RequestMapping("/api/alarmtags")
@RestController
public class AlarmTagController {
	private final AlarmTagQueryPort alarmTagQueryPort;
	private final AlarmTagCommandUseCase alarmTagCommandUseCase;

	public AlarmTagController(
		AlarmTagQueryPort alarmTagQueryPort,
		AlarmTagCommandUseCase alarmTagCommandUseCase) {

		this.alarmTagQueryPort = alarmTagQueryPort;
		this.alarmTagCommandUseCase = alarmTagCommandUseCase;
	}

	@GetMapping
	public List<AlarmTagResponseDto> getMyTags(
		@AuthenticationPrincipal
		JwtAuthentication principal) {

		return alarmTagQueryPort.getMyAlarmTags(principal.id());
	}

	@PostMapping
	public void add(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		@Valid
		@RequestBody
		AddAlarmTagRequestDto requestDto) {

		alarmTagCommandUseCase.add(principal.id(), requestDto.word());
	}

	@DeleteMapping
	public void remove(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		@Valid
		@RequestBody
		AddAlarmTagRequestDto requestDto
	) {
		alarmTagCommandUseCase.remove(principal.id(), requestDto.word());
	}
}
