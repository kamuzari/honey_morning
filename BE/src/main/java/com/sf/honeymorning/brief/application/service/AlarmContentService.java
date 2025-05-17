package com.sf.honeymorning.brief.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.alarm.application.service.dto.response.AiResponseDto;
import com.sf.honeymorning.brief.application.port.in.AlarmContentCommandUseCase;
import com.sf.honeymorning.brief.application.port.out.CommandBriefingPort;
import com.sf.honeymorning.brief.application.port.out.ValidBriefingContentPort;
import com.sf.honeymorning.common.event.producer.EventDispatcherHandler;

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

		EventDispatcherHandler.raise(briefingId);
	}
}
