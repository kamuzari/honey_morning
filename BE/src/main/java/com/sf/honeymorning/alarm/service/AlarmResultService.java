package com.sf.honeymorning.alarm.service;

import static com.sf.honeymorning.common.exception.model.constant.ErrorProtocol.POLICY_VIOLATION;
import static java.text.MessageFormat.format;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.alarm.controller.dto.request.AddAlarmResultRequestDto;
import com.sf.honeymorning.alarm.controller.dto.response.AlarmResultResponseDto;
import com.sf.honeymorning.alarm.domain.entity.AlarmResult;
import com.sf.honeymorning.alarm.domain.entity.UserAlarmResultStreak;
import com.sf.honeymorning.alarm.domain.repository.AlarmResultRepository;
import com.sf.honeymorning.alarm.domain.repository.UserAlarmResultStreakRepository;
import com.sf.honeymorning.alarm.service.mapper.AlarmResultMapper;
import com.sf.honeymorning.common.exception.model.NotFoundResourceException;
import com.sf.honeymorning.user.adapter.out.persistence.repository.UserRepository;

@Transactional(readOnly = true)
@Service
public class AlarmResultService {

	private final AlarmResultRepository alarmResultRepository;
	private final UserRepository userRepository;
	private final UserAlarmResultStreakRepository userAlarmResultStreakRepository;

	private final AlarmResultMapper alarmResultMapper;

	public AlarmResultService(AlarmResultRepository alarmResultRepository,
		UserRepository userRepository,
		UserAlarmResultStreakRepository userAlarmResultStreakRepository,
		AlarmResultMapper alarmResultMapper) {

		this.alarmResultRepository = alarmResultRepository;
		this.userRepository = userRepository;
		this.userAlarmResultStreakRepository = userAlarmResultStreakRepository;
		this.alarmResultMapper = alarmResultMapper;
	}

	public List<AlarmResultResponseDto> getPageContent(Long userId, Long lastId) {
		return alarmResultRepository.findNextPage(userId, lastId).stream()
			.map(alarmResultMapper::toAlarmResultResponseDto)
			.toList();
	}

	@Transactional
	public void add(Long userId, AddAlarmResultRequestDto requestDto) {
		var userAlarmResultStreak = userAlarmResultStreakRepository.findByUserId(userId)
			.orElseGet(() -> UserAlarmResultStreak.initialize(userId));
		userAlarmResultStreak.countConsecutiveDays(LocalDateTime.now());
		var savedUserAlarmResultStreak = userAlarmResultStreakRepository.save(userAlarmResultStreak);

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
			.updateMaximumStreak(savedUserAlarmResultStreak.getConsecutiveDays());
	}

	public int getMaximumStreak(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new NotFoundResourceException(
				format("존재하지 않는 사용자입니다. userId -> {0}", userId)
				, POLICY_VIOLATION))
			.getMaxStreak();
	}
}
