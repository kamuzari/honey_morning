package com.honeymorning.api.alarm.adapter.out.persistence;

import static com.honeymorning.common.exception.constant.ErrorProtocol.POLICY_VIOLATION;
import static java.text.MessageFormat.format;

import java.util.List;

import org.springframework.stereotype.Component;

import com.honeymorning.api.alarm.adapter.in.web.dto.response.AlarmResultResponseDto;
import com.honeymorning.api.alarm.adapter.in.web.port.out.AlarmResultQueryPort;
import com.honeymorning.api.alarm.adapter.out.persistence.mapper.AlarmResultPersistenceMapper;
import com.honeymorning.api.alarm.adapter.out.persistence.repository.AlarmResultRepository;
import com.honeymorning.api.user.adapter.out.persistence.repository.UserRepository;
import com.honeymorning.common.exception.NotFoundResourceException;

@Component
public class AlarmResultPersistenceAdapter implements AlarmResultQueryPort {
	private final AlarmResultRepository alarmResultRepository;
	private final UserRepository userRepository;
	private final AlarmResultPersistenceMapper alarmResultPersistenceMapper;

	public AlarmResultPersistenceAdapter(
		AlarmResultRepository alarmResultRepository,
		UserRepository userRepository,
		AlarmResultPersistenceMapper alarmResultPersistenceMapper) {

		this.alarmResultRepository = alarmResultRepository;
		this.userRepository = userRepository;
		this.alarmResultPersistenceMapper = alarmResultPersistenceMapper;
	}

	public List<AlarmResultResponseDto> getMyAlarmResults(Long userId, Long lastId) {
		return alarmResultRepository.findNextPage(userId, lastId).stream()
			.map(alarmResultPersistenceMapper::toAlarmResultResponseDto)
			.toList();
	}

	public int getMaximumStreak(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new NotFoundResourceException(
				format("존재하지 않는 사용자입니다. userId -> {0}", userId)
				, POLICY_VIOLATION))
			.getMaxStreak();
	}
}
