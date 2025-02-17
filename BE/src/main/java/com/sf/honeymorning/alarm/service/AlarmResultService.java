package com.sf.honeymorning.alarm.service;

import static com.sf.honeymorning.common.exception.model.ErrorProtocol.POLICY_VIOLATION;
import static java.text.MessageFormat.format;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.alarm.controller.dto.request.AlarmResultRequestCreateDto;
import com.sf.honeymorning.alarm.controller.dto.response.AlarmResultResponseDto;
import com.sf.honeymorning.alarm.domain.entity.AlarmResult;
import com.sf.honeymorning.alarm.domain.entity.UserAlarmResultStreak;
import com.sf.honeymorning.alarm.domain.repository.AlarmResultRepository;
import com.sf.honeymorning.alarm.domain.repository.UserAlarmResultStreakRepository;
import com.sf.honeymorning.common.exception.model.NotFoundResourceException;
import com.sf.honeymorning.user.repository.UserRepository;

@Transactional(readOnly = true)
@Service
public class AlarmResultService {

	private final AlarmResultRepository alarmResultRepository;
	private final UserRepository userRepository;
	private final UserAlarmResultStreakRepository userAlarmResultStreakRepository;

	public AlarmResultService(AlarmResultRepository alarmResultRepository, UserRepository userRepository,
		UserAlarmResultStreakRepository userAlarmResultStreakRepository) {
		this.alarmResultRepository = alarmResultRepository;
		this.userRepository = userRepository;
		this.userAlarmResultStreakRepository = userAlarmResultStreakRepository;
	}

	public List<AlarmResultResponseDto> getContents(Long userId, Long lastId) {
		return alarmResultRepository.findNextPage(userId, lastId).stream()
			.map(alarmResult -> new AlarmResultResponseDto(
					alarmResult.getCount(),
					alarmResult.isAttended(),
					alarmResult.getCreatedAt()
				)
			).toList();
	}

	@Transactional
	public void add(Long userId, AlarmResultRequestCreateDto requestDto) {
		LocalDateTime now = LocalDateTime.now();
		UserAlarmResultStreak userAlarmResultStreak = userAlarmResultStreakRepository.findByUserId(userId).orElseThrow(
			() -> new NotFoundResourceException(
				format("스트릭 정보가 존재했어야 합니다.. userId -> {0}", userId)
				, POLICY_VIOLATION)

		);
		// RedisRepository
		userAlarmResultStreak.countConsecutiveDays(now);
		userAlarmResultStreakRepository.save(userAlarmResultStreak);

		alarmResultRepository.save(new AlarmResult(
			userId,
			requestDto.briefingId(),
			requestDto.matchCount(),
			true
		));

		userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("존재하지 않는 사용자입니다. userId -> {0}", userId)
				, POLICY_VIOLATION))
			.updateMaximumStreak(userAlarmResultStreak.getConsecutiveDays());
	}

	public int getMaximumStreak(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new NotFoundResourceException(
				format("존재하지 않는 사용자입니다. userId -> {0}", userId)
				, POLICY_VIOLATION))
			.getMaxStreak();
	}
}
