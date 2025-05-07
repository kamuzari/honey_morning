package com.sf.honeymorning.alarm.adapter.out.persistence;

import static com.sf.honeymorning.common.exception.model.constant.ErrorProtocol.POLICY_VIOLATION;
import static java.text.MessageFormat.format;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.alarm.adapter.in.web.dto.response.AlarmResultResponseDto;
import com.sf.honeymorning.alarm.adapter.in.web.port.out.AlarmResultQueryPort;
import com.sf.honeymorning.alarm.adapter.out.persistence.repository.AlarmResultRepository;
import com.sf.honeymorning.alarm.application.service.mapper.AlarmResultMapper;
import com.sf.honeymorning.common.exception.model.NotFoundResourceException;
import com.sf.honeymorning.user.adapter.out.persistence.repository.UserRepository;

@Component
public class AlarmResultPersistenceAdapter implements AlarmResultQueryPort {
	private final AlarmResultRepository alarmResultRepository;
	private final UserRepository userRepository;
	private final AlarmResultMapper alarmResultMapper;

	public AlarmResultPersistenceAdapter(
		AlarmResultRepository alarmResultRepository,
		UserRepository userRepository,
		AlarmResultMapper alarmResultMapper) {

		this.alarmResultRepository = alarmResultRepository;
		this.userRepository = userRepository;
		this.alarmResultMapper = alarmResultMapper;
	}

	public List<AlarmResultResponseDto> getMyAlarmResults(Long userId, Long lastId) {
		return alarmResultRepository.findNextPage(userId, lastId).stream()
			.map(alarmResultMapper::toAlarmResultResponseDto)
			.toList();
	}

	public int getMaximumStreak(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new NotFoundResourceException(
				format("존재하지 않는 사용자입니다. userId -> {0}", userId)
				, POLICY_VIOLATION))
			.getMaxStreak();
	}
}
