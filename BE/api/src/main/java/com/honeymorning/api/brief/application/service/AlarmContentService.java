package com.honeymorning.api.brief.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.honeymorning.api.alarm.application.service.dto.response.AiResponseDto;
import com.honeymorning.api.brief.adapter.in.event.dto.BriefingSearchCommandDto;
import com.honeymorning.api.brief.adapter.in.event.dto.BriefingTtsCommandDto;
import com.honeymorning.api.brief.application.port.in.AlarmContentCommandUseCase;
import com.honeymorning.api.brief.application.port.out.CommandBriefingPort;
import com.honeymorning.api.brief.application.port.out.ValidBriefingContentPort;
import com.honeymorning.api.common.event.handler.EventDispatcherHandler;

@Transactional(readOnly = true)
@Service
public class AlarmContentService implements AlarmContentCommandUseCase {
	private final ValidBriefingContentPort validBriefingContentPort;
	private final CommandBriefingPort commandBriefingPort;

	public AlarmContentService(
		ValidBriefingContentPort validBriefingContentPort,
		CommandBriefingPort commandBriefingPort) {

		this.validBriefingContentPort = validBriefingContentPort;
		this.commandBriefingPort = commandBriefingPort;
	}

	@Transactional(rollbackFor = Exception.class)
	public void create(AiResponseDto aiResponseDto) {
		validBriefingContentPort.verifyStillAliveAlarm(aiResponseDto.userId());
		Long briefingId = commandBriefingPort.create(aiResponseDto);

		EventDispatcherHandler.raise(new BriefingTtsCommandDto(briefingId));
		EventDispatcherHandler.raise(new BriefingSearchCommandDto(briefingId));
	}
}
