package com.sf.honeymorning.alarm.service;

import static com.sf.honeymorning.common.exception.model.ErrorProtocol.BUSINESS_VIOLATION;
import static com.sf.honeymorning.common.exception.model.ErrorProtocol.POLICY_VIOLATION;
import static java.text.MessageFormat.format;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.alarm.controller.dto.request.AlarmSetRequest;
import com.sf.honeymorning.alarm.controller.dto.response.AlarmResponse;
import com.sf.honeymorning.alarm.domain.entity.Alarm;
import com.sf.honeymorning.alarm.domain.repository.AlarmRepository;
import com.sf.honeymorning.alarm.exception.AlarmBusinessException;
import com.sf.honeymorning.common.exception.model.NotFoundResourceException;

@Service
@Transactional(readOnly = true)
public class AlarmService {

	private final AlarmRepository alarmRepository;

	public AlarmService(AlarmRepository alarmRepository) {
		this.alarmRepository = alarmRepository;
	}

	public AlarmResponse getMyAlarmWithMyTags(Long userId) {
		Alarm alarm = alarmRepository.findByUserId(userId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("알람이 반드시 존재했어야합니다. userId -> {0}", userId)
				, POLICY_VIOLATION));

		return new AlarmResponse(
			alarm.getId(),
			alarm.getWakeUpTime(),
			alarm.getDayOfTheWeeks(),
			alarm.getRepeatFrequency(),
			alarm.getRepeatInterval(),
			alarm.isActive()
		);
	}

	@Transactional
	public void set(AlarmSetRequest alarmRequestDto, Long userId) {
		Alarm alarm = alarmRepository.findByUserId(userId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("알람이 반드시 존재했어야합니다. userId -> {0}", userId)
				, POLICY_VIOLATION));

		alarm.set(alarmRequestDto.alarmTime(),
			alarmRequestDto.weekdays(),
			alarmRequestDto.repeatFrequency(),
			alarmRequestDto.repeatInterval(),
			alarmRequestDto.isActive());
	}

	public void verifySleepMode(Long userId, LocalDateTime sleepStartAt) {
		Alarm alarm = alarmRepository.findByUserIdAndIsActiveTrue(userId)
			.orElseThrow(() -> new NotFoundResourceException("알람이 활성화되지 않았습니다.", BUSINESS_VIOLATION));

		if (!alarm.canSleepMode(sleepStartAt)) {
			throw new AlarmBusinessException("수면 모드는 거부되었습니다.", POLICY_VIOLATION);
		}
	}
}
