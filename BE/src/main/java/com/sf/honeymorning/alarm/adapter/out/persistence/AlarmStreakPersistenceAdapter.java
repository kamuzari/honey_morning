package com.sf.honeymorning.alarm.adapter.out.persistence;

import static com.sf.honeymorning.common.exception.model.constant.ErrorProtocol.POLICY_VIOLATION;
import static java.text.MessageFormat.format;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.alarm.adapter.out.persistence.entity.AlarmResultEntity;
import com.sf.honeymorning.alarm.adapter.out.persistence.entity.UserAlarmResultStreakEntity;
import com.sf.honeymorning.alarm.adapter.out.persistence.mapper.AlarmStreakPersistenceMapper;
import com.sf.honeymorning.alarm.adapter.out.persistence.repository.AlarmResultRepository;
import com.sf.honeymorning.alarm.adapter.out.persistence.repository.UserAlarmResultStreakRepository;
import com.sf.honeymorning.alarm.application.domain.User;
import com.sf.honeymorning.alarm.application.domain.UserAlarmStreak;
import com.sf.honeymorning.alarm.application.port.out.CommandAlarmResultPort;
import com.sf.honeymorning.alarm.application.port.out.LoadAlarmResultPort;
import com.sf.honeymorning.common.exception.model.NotFoundResourceException;
import com.sf.honeymorning.user.adapter.out.persistence.entity.UserEntity;
import com.sf.honeymorning.user.adapter.out.persistence.repository.UserRepository;

@Component
public class AlarmStreakPersistenceAdapter implements CommandAlarmResultPort, LoadAlarmResultPort {
	private final AlarmResultRepository alarmResultRepository;
	private final UserRepository userRepository;
	private final UserAlarmResultStreakRepository userAlarmResultStreakRepository;
	private final AlarmStreakPersistenceMapper alarmStreakPersistenceMapper;

	public AlarmStreakPersistenceAdapter(
		AlarmResultRepository alarmResultRepository,
		UserRepository userRepository,
		UserAlarmResultStreakRepository userAlarmResultStreakRepository,
		AlarmStreakPersistenceMapper alarmStreakPersistenceMapper) {

		this.alarmResultRepository = alarmResultRepository;
		this.userRepository = userRepository;
		this.userAlarmResultStreakRepository = userAlarmResultStreakRepository;
		this.alarmStreakPersistenceMapper = alarmStreakPersistenceMapper;
	}

	public void addTodayAlarmResults(Long userId, Long briefingId, Integer matchCount) {
		alarmResultRepository.save(new AlarmResultEntity(
			userId,
			briefingId,
			matchCount,
			true
		));
	}

	@Override
	public int reflect(UserAlarmStreak userAlarmStreak) {
		return userAlarmResultStreakRepository.save(
			new UserAlarmResultStreakEntity(
				userAlarmStreak.getUserId(),
				userAlarmStreak.getLatestAt(),
				userAlarmStreak.getConsecutiveDays()
			)
		).getConsecutiveDays();
	}

	@Override
	public void reflect(User user) {
		UserEntity userEntity = new UserEntity(
			user.getId(),
			user.getUsername(),
			user.getPassword(),
			user.getNickName(),
			user.getMaximumStreak(),
			user.getRole()
		);
		userRepository.save(userEntity);
	}

	public User getUser(Long userId) {
		UserEntity userEntity = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("존재하지 않는 사용자입니다. userId -> {0}", userId)
				, POLICY_VIOLATION));

		return alarmStreakPersistenceMapper.toDomain(userEntity);
	}


	public UserAlarmStreak getUserAlarmResultStreak(Long userId) {
		var userAlarmResultStreakEntity = userAlarmResultStreakRepository.findByUserId(userId)
			.orElseGet(() -> UserAlarmResultStreakEntity.initialize(userId));

		return alarmStreakPersistenceMapper.toDomain(userAlarmResultStreakEntity);
	}

}
