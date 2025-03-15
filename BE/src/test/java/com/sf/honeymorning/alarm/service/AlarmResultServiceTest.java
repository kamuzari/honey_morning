package com.sf.honeymorning.alarm.service;

import static com.sf.honeymorning.user.entity.UserRole.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.sf.honeymorning.alarm.controller.dto.request.AddAlarmResultRequestDto;
import com.sf.honeymorning.alarm.domain.entity.UserAlarmResultStreak;
import com.sf.honeymorning.alarm.domain.repository.AlarmResultRepository;
import com.sf.honeymorning.alarm.domain.repository.UserAlarmResultStreakRepository;
import com.sf.honeymorning.context.MockTestServiceEnvironment;
import com.sf.honeymorning.user.entity.User;
import com.sf.honeymorning.user.repository.UserRepository;

class AlarmResultServiceTest extends MockTestServiceEnvironment {
	@InjectMocks
	AlarmResultService sut;

	@Mock
	AlarmResultRepository alarmResultRepository;

	@Mock
	UserRepository userRepository;

	@Mock
	UserAlarmResultStreakRepository userAlarmResultStreakRepository;

	@Test
	@DisplayName("알람 결과를 추가하면, 만약 어제와 오늘이 연속적이면 연속 출결일수가 증가한다")
	void testAdd() {
		//given
		int expectedMaxConsecutiveDays = 2;
		var userAlarmResultStreak = new UserAlarmResultStreak(AUTH_USER.getId(), LocalDateTime.now().minusDays(1).plusMinutes(1), 1);
		var addAlarmResultRequestDto = new AddAlarmResultRequestDto(1L, 2);
		var user = new User(FAKER_DATE_FACTORY.name().username(), FAKER_DATE_FACTORY.internet().password(), "DeepSeek",
			ROLE_USER);

		given(userAlarmResultStreakRepository.save(userAlarmResultStreak)).willReturn(userAlarmResultStreak);
		given(userAlarmResultStreakRepository.findByUserId(AUTH_USER.getId())).willReturn(
			Optional.of(userAlarmResultStreak));
		given(userRepository.findById(AUTH_USER.getId())).willReturn(Optional.of(user));

		//when
		sut.add(AUTH_USER.getId(), addAlarmResultRequestDto);

		//then
		assertThat(user.getMaxStreak()).isEqualTo(expectedMaxConsecutiveDays);
	}

	@Test
	@DisplayName("알람 결과를 추가하면, 만약 어제와 오늘이 연속적이지 않으면 연속 출결일수가 다시 1이 된다")
	void testAddNotIncrement() {
		//given
		int expectedConsecutiveDays = 1;
		var userAlarmResultStreak = new UserAlarmResultStreak(AUTH_USER.getId(), LocalDateTime.now().minusDays(2), 1);
		var addAlarmResultRequestDto = new AddAlarmResultRequestDto(1L, 2);
		var user = new User(FAKER_DATE_FACTORY.name().username(), FAKER_DATE_FACTORY.internet().password(), "DeepSeek",
			ROLE_USER);

		given(userAlarmResultStreakRepository.findByUserId(AUTH_USER.getId())).willReturn(
			Optional.of(userAlarmResultStreak));
		given(userAlarmResultStreakRepository.save(userAlarmResultStreak)).willReturn(userAlarmResultStreak);
		given(userRepository.findById(AUTH_USER.getId())).willReturn(Optional.of(user));

		//when
		sut.add(AUTH_USER.getId(), addAlarmResultRequestDto);

		//then
		assertThat(user.getMaxStreak()).isEqualTo(expectedConsecutiveDays);
	}

	@Test
	@DisplayName("알람 결과를 추가할때, 저장된 스트링 정보가 없으면 최초 자동 생성된다")
	void testAddInitializeStreakResult() {
		//given
		int expectedConsecutiveDays = 1;
		var userAlarmResultStreak = new UserAlarmResultStreak(AUTH_USER.getId(), LocalDateTime.now().minusDays(2), 1);
		var addAlarmResultRequestDto = new AddAlarmResultRequestDto(1L, 2);
		var user = new User(FAKER_DATE_FACTORY.name().username(), FAKER_DATE_FACTORY.internet().password(), "DeepSeek",
			ROLE_USER);

		given(userAlarmResultStreakRepository.save(any())).willReturn(userAlarmResultStreak);
		given(userRepository.findById(AUTH_USER.getId())).willReturn(Optional.of(user));

		//when
		sut.add(AUTH_USER.getId(), addAlarmResultRequestDto);

		//then
		assertThat(user.getMaxStreak()).isEqualTo(expectedConsecutiveDays);
	}

	@Test
	@DisplayName("최대 스트릭 일수를 가져온다")
	void testGetMaximumStreak() {
		//given
		User user = new User(FAKER_DATE_FACTORY.name().username(),
			FAKER_DATE_FACTORY.internet().password(),
			"DeepSeek",
			ROLE_USER);
		given(userRepository.findById(AUTH_USER.getId())).willReturn(Optional.of(user));

		//when
		int maximumStreak = sut.getMaximumStreak(AUTH_USER.getId());

		//then
		assertThat(maximumStreak).isEqualTo(user.getMaxStreak());
	}

}