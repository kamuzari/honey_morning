package com.sf.honeymorning.alarm.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.alarm.adapter.in.web.dto.request.AddAlarmResultRequestDto;
import com.sf.honeymorning.alarm.application.domain.UpdateStreakUser;
import com.sf.honeymorning.alarm.application.domain.CreateUserAlarmStreak;
import com.sf.honeymorning.alarm.application.port.in.AlarmResultCommandUseCase;
import com.sf.honeymorning.alarm.application.port.out.CommandAlarmResultPort;
import com.sf.honeymorning.alarm.application.port.out.LoadAlarmResultPort;
import com.sf.honeymorning.alarm.application.service.mapper.AlarmResultMapper;

@Transactional(readOnly = true)
@Service
public class AlarmResultService implements AlarmResultCommandUseCase {

	private final CommandAlarmResultPort commandAlarmResultPort;
	private final LoadAlarmResultPort loadAlarmResultPort;
	private final AlarmResultMapper alarmResultMapper;

	public AlarmResultService(
		CommandAlarmResultPort commandAlarmResultPort,
		LoadAlarmResultPort loadAlarmResultPort,
		AlarmResultMapper alarmResultMapper) {

		this.commandAlarmResultPort = commandAlarmResultPort;
		this.loadAlarmResultPort = loadAlarmResultPort;
		this.alarmResultMapper = alarmResultMapper;
	}

	@Transactional
	public void add(Long userId, AddAlarmResultRequestDto requestDto) {
		var addAlarmResult = alarmResultMapper.toAddAlarmResult(userId, requestDto);
		commandAlarmResultPort.addTodayAlarmResults(addAlarmResult);

		CreateUserAlarmStreak userAlarmResultStreak = loadAlarmResultPort.getUserAlarmResultStreak(userId);
		userAlarmResultStreak.countConsecutiveDays(LocalDateTime.now());
		int consecutiveDays = commandAlarmResultPort.reflect(userAlarmResultStreak);

		UpdateStreakUser updateStreakUser = loadAlarmResultPort.getUser(userId);
		updateStreakUser.updateMaximumStreak(consecutiveDays);
		commandAlarmResultPort.reflect(updateStreakUser);
	}

}
