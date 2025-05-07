package com.sf.honeymorning.alarm.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.alarm.adapter.in.web.dto.request.AddAlarmResultRequestDto;
import com.sf.honeymorning.alarm.application.domain.User;
import com.sf.honeymorning.alarm.application.domain.UserAlarmStreak;
import com.sf.honeymorning.alarm.application.port.in.AlarmResultCommandUseCase;
import com.sf.honeymorning.alarm.application.port.out.CommandAlarmResultPort;
import com.sf.honeymorning.alarm.application.port.out.LoadAlarmResultPort;

@Transactional(readOnly = true)
@Service
public class AlarmResultService implements AlarmResultCommandUseCase {
	private final CommandAlarmResultPort commandAlarmResultPort;
	private final LoadAlarmResultPort loadAlarmResultPort;

	public AlarmResultService(
		CommandAlarmResultPort commandAlarmResultPort,
		LoadAlarmResultPort loadAlarmResultPort) {

		this.commandAlarmResultPort = commandAlarmResultPort;
		this.loadAlarmResultPort = loadAlarmResultPort;
	}

	@Transactional
	public void add(Long userId, AddAlarmResultRequestDto requestDto) {
		commandAlarmResultPort.addTodayAlarmResults(userId, requestDto.briefingId(), requestDto.matchCount());

		UserAlarmStreak userAlarmResultStreak = loadAlarmResultPort.getUserAlarmResultStreak(userId);
		userAlarmResultStreak.countConsecutiveDays(LocalDateTime.now());
		int consecutiveDays = commandAlarmResultPort.reflect(userAlarmResultStreak);

		User user = loadAlarmResultPort.getUser(userId);
		user.updateMaximumStreak(consecutiveDays);
		commandAlarmResultPort.reflect(user);
	}
}
