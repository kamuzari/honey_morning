package com.sf.honeymorning.alarm.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.alarm.application.port.in.AlarmTagCommandUseCase;
import com.sf.honeymorning.alarm.application.port.out.CommandAlarmTagPort;
import com.sf.honeymorning.alarm.application.port.out.ValidationAlarmTagPort;

@Service
@Transactional(readOnly = true)
public class AlarmTagService implements AlarmTagCommandUseCase {
	private final ValidationAlarmTagPort validationAlarmTagPort;
	private final CommandAlarmTagPort commandAlarmTagPort;

	public AlarmTagService(
		ValidationAlarmTagPort validationAlarmTagPort,
		CommandAlarmTagPort commandAlarmTagPort) {

		this.validationAlarmTagPort = validationAlarmTagPort;
		this.commandAlarmTagPort = commandAlarmTagPort;
	}

	@Transactional
	public void add(Long userId, String word) {
		validationAlarmTagPort.validate(userId, word);
		commandAlarmTagPort.add(userId, word);
	}

	@Transactional
	public void remove(Long userId, String word) {
		commandAlarmTagPort.remove(userId, word);
	}
}
