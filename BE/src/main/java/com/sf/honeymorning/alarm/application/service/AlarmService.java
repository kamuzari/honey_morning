package com.sf.honeymorning.alarm.application.service;

import static com.sf.honeymorning.common.exception.model.constant.ErrorProtocol.POLICY_VIOLATION;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.alarm.adapter.in.web.dto.request.AlarmSetRequest;
import com.sf.honeymorning.alarm.application.domain.UpdateAlarm;
import com.sf.honeymorning.alarm.application.domain.VerifySleepModeAlarm;
import com.sf.honeymorning.alarm.application.port.in.AlarmCommandUseCase;
import com.sf.honeymorning.alarm.application.port.in.ValidateAlarmUseCase;
import com.sf.honeymorning.alarm.application.port.out.CommandAlarmPort;
import com.sf.honeymorning.alarm.application.port.out.LoadAlarmPort;
import com.sf.honeymorning.common.exception.model.BusinessException;

@Service
@Transactional(readOnly = true)
public class AlarmService implements AlarmCommandUseCase, ValidateAlarmUseCase {
	private final LoadAlarmPort loadAlarmPort;
	private final CommandAlarmPort commandAlarmPort;

	public AlarmService(
		LoadAlarmPort loadAlarmPort,
		CommandAlarmPort commandAlarmPort) {

		this.loadAlarmPort = loadAlarmPort;
		this.commandAlarmPort = commandAlarmPort;
	}

	@Transactional
	public void update(AlarmSetRequest alarmRequestDto, Long userId) {
		UpdateAlarm updateAlarm = loadAlarmPort.getAlarm(userId);

		updateAlarm.update(
			alarmRequestDto.alarmTime(),
			alarmRequestDto.weekdays(),
			alarmRequestDto.repeatFrequency(),
			alarmRequestDto.repeatInterval(),
			alarmRequestDto.isActive()
		);

		commandAlarmPort.reflect(updateAlarm);
	}

	public void verifySleepMode(Long userId, LocalDateTime sleepStartAt) {
		VerifySleepModeAlarm alarm = loadAlarmPort.getActivatedAlarm(userId);

		if (!alarm.canSleepMode(sleepStartAt)) {
			throw new BusinessException("수면 모드는 거부되었습니다.", POLICY_VIOLATION);
		}
	}
}
