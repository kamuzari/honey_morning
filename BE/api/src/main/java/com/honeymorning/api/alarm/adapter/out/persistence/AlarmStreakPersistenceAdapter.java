package com.honeymorning.api.alarm.adapter.out.persistence;

import static com.honeymorning.api.common.exception.model.constant.ErrorProtocol.POLICY_VIOLATION;
import static java.text.MessageFormat.format;

import org.springframework.stereotype.Component;

import com.honeymorning.api.alarm.adapter.out.persistence.entity.AlarmResultEntity;
import com.honeymorning.api.alarm.adapter.out.persistence.entity.UserAlarmResultStreakEntity;
import com.honeymorning.api.alarm.adapter.out.persistence.mapper.AlarmStreakPersistenceMapper;
import com.honeymorning.api.alarm.adapter.out.persistence.repository.AlarmResultRepository;
import com.honeymorning.api.alarm.adapter.out.persistence.repository.UserAlarmResultStreakRepository;
import com.honeymorning.api.alarm.application.domain.AddAlarmResult;
import com.honeymorning.api.alarm.application.domain.CreateUserAlarmStreak;
import com.honeymorning.api.alarm.application.domain.UpdateStreakUser;
import com.honeymorning.api.alarm.application.port.out.CommandAlarmResultPort;
import com.honeymorning.api.alarm.application.port.out.LoadAlarmResultPort;
import com.honeymorning.api.common.exception.model.NotFoundResourceException;
import com.honeymorning.api.user.adapter.out.persistence.entity.UserEntity;
import com.honeymorning.api.user.adapter.out.persistence.repository.UserRepository;

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

	@Override
	public void addTodayAlarmResults(AddAlarmResult addAlarmResult) {
		alarmResultRepository.save(
			new AlarmResultEntity(
				addAlarmResult.userId(),
				addAlarmResult.briefingId(),
				addAlarmResult.matchCount(),
				true
			)
		);
	}

	@Override
	public int reflect(CreateUserAlarmStreak createUserAlarmStreak) {
		return userAlarmResultStreakRepository.save(
			new UserAlarmResultStreakEntity(
				createUserAlarmStreak.getUserId(),
				createUserAlarmStreak.getLatestAt(),
				createUserAlarmStreak.getConsecutiveDays()
			)
		).getConsecutiveDays();
	}

	@Override
	public void reflect(UpdateStreakUser updateStreakUser) {
		UserEntity userEntity = userRepository.findById(updateStreakUser.getId())
			.orElseThrow(() -> new NotFoundResourceException(
				format("존재하지 않는 사용자입니다. userId -> {0}", updateStreakUser.getId())
				, POLICY_VIOLATION));

		userEntity.updateMaximumStreak(updateStreakUser.getMaximumStreak());
	}

	public UpdateStreakUser getUser(Long userId) {
		UserEntity userEntity = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("존재하지 않는 사용자입니다. userId -> {0}", userId)
				, POLICY_VIOLATION));

		return alarmStreakPersistenceMapper.toDomain(userEntity);
	}

	public CreateUserAlarmStreak getUserAlarmResultStreak(Long userId) {
		var userAlarmResultStreakEntity = userAlarmResultStreakRepository.findByUserId(userId)
			.orElseGet(() -> UserAlarmResultStreakEntity.initialize(userId));

		return alarmStreakPersistenceMapper.toDomain(userAlarmResultStreakEntity);
	}

}
