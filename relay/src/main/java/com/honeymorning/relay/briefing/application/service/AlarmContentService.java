package com.honeymorning.relay.briefing.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.honeymorning.relay.briefing.application.port.in.AlarmContentCommandUseCase;
import com.honeymorning.relay.briefing.application.port.out.CommandBriefingPort;
import com.honeymorning.relay.briefing.application.port.out.ValidBriefingContentPort;
import com.honeymorning.relay.briefing.application.service.dto.AiResponseDto;

@Transactional(readOnly = true)
@Service
public class AlarmContentService implements AlarmContentCommandUseCase {
	private final ValidBriefingContentPort validBriefingContentPort;
	private final CommandBriefingPort commandBriefingPort;

	public AlarmContentService(
		ValidBriefingContentPort validBriefingContentPort,
		CommandBriefingPort commandBriefingPort
	) {
		this.validBriefingContentPort = validBriefingContentPort;
		this.commandBriefingPort = commandBriefingPort;
	}

	@Transactional
	public void create(AiResponseDto aiResponseDto) {
		if (validBriefingContentPort.isStillAliveAlarm(aiResponseDto.userId())) {
			commandBriefingPort.create(aiResponseDto);
		}
	}
}
