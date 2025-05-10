package com.sf.honeymorning.alarm.application.port.in.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.sf.honeymorning.alarm.adapter.in.web.dto.request.AddAlarmResultRequestDto;
import com.sf.honeymorning.alarm.application.domain.User;
import com.sf.honeymorning.alarm.application.domain.UserAlarmStreak;
import com.sf.honeymorning.alarm.application.port.in.AlarmResultCommandUseCase;
import com.sf.honeymorning.alarm.application.port.out.CommandAlarmResultPort;
import com.sf.honeymorning.alarm.application.port.out.LoadAlarmResultPort;
import com.sf.honeymorning.alarm.application.service.AlarmResultService;
import com.sf.honeymorning.context.mock.MockTest;
import com.sf.honeymorning.user.adapter.out.persistence.entity.UserRole;

class AlarmResultCommandUseCaseTest extends MockTest {

	AlarmResultCommandUseCase sut;

	@InjectMocks
	AlarmResultService alarmResultService;

	@Mock
	LoadAlarmResultPort loadAlarmResultPort;

	@Mock
	CommandAlarmResultPort commandAlarmResultPort;

	@BeforeEach
	void setUp() {
		this.sut = alarmResultService;
	}

	@Test
	@DisplayName("연속적으로 퀴즈를 풀이했다면 스트릭 일수가 1증가한다")
	void testIncreaseStreak() {
		//given
		AddAlarmResultRequestDto requestDto = new AddAlarmResultRequestDto(
			1L,
			2
		);
		int initialConsecutiveDay = 0;
		UserAlarmStreak expectedUserAlarmStreak = new UserAlarmStreak(
			AUTH_USER_ENTITY.getId(),
			LocalDateTime.now(),
			initialConsecutiveDay);
		given(loadAlarmResultPort.getUserAlarmResultStreak(AUTH_USER_ENTITY.getId())).willReturn(
			expectedUserAlarmStreak
		);
		given(loadAlarmResultPort.getUser(AUTH_USER_ENTITY.getId())).willReturn(new User(
			AUTH_USER_ENTITY.getId(),
			AUTH_USER_ENTITY.getUsername(),
			AUTH_USER_ENTITY.getPassword(),
			AUTH_USER_ENTITY.getNickName(),
			initialConsecutiveDay,
			UserRole.ROLE_USER
		));
		//when
		sut.add(AUTH_USER_ENTITY.getId(), requestDto);
		//then
		assertThat(expectedUserAlarmStreak.getConsecutiveDays()).isEqualTo(initialConsecutiveDay + 1);
	}

	@Test
	@DisplayName("연속적으로 퀴즈를 풀이하지 않으면 연속 스트릭이 1로 돌아간다")
	void testKeepStreak() {
		//given
		AddAlarmResultRequestDto requestDto = new AddAlarmResultRequestDto(
			1L,
			2
		);
		int consecutiveDay = 3;
		UserAlarmStreak expectedUserAlarmStreak = new UserAlarmStreak(
			AUTH_USER_ENTITY.getId(),
			LocalDateTime.now().minusDays(2),
			consecutiveDay);
		given(loadAlarmResultPort.getUserAlarmResultStreak(AUTH_USER_ENTITY.getId())).willReturn(
			expectedUserAlarmStreak
		);
		given(loadAlarmResultPort.getUser(AUTH_USER_ENTITY.getId())).willReturn(new User(
			AUTH_USER_ENTITY.getId(),
			AUTH_USER_ENTITY.getUsername(),
			AUTH_USER_ENTITY.getPassword(),
			AUTH_USER_ENTITY.getNickName(),
			consecutiveDay,
			UserRole.ROLE_USER
		));
		//when
		sut.add(AUTH_USER_ENTITY.getId(), requestDto);
		//then
		assertThat(expectedUserAlarmStreak.getConsecutiveDays()).isEqualTo(1);
	}

	@Test
	@DisplayName("연속적으로 퀴즈를 풀이하여 최대 스트릭을 갱신하면 사용자의 최대 스트릭이 이에 반영된다")
	void testUpdateUserMaximumStreak() {
		//given
		AddAlarmResultRequestDto requestDto = new AddAlarmResultRequestDto(
			1L,
			2
		);
		int consecutiveDay = 3;
		UserAlarmStreak expectedUserAlarmStreak = new UserAlarmStreak(
			AUTH_USER_ENTITY.getId(),
			LocalDateTime.now(),
			consecutiveDay);
		User user = new User(
			AUTH_USER_ENTITY.getId(),
			AUTH_USER_ENTITY.getUsername(),
			AUTH_USER_ENTITY.getPassword(),
			AUTH_USER_ENTITY.getNickName(),
			consecutiveDay,
			UserRole.ROLE_USER
		);

		given(loadAlarmResultPort.getUserAlarmResultStreak(AUTH_USER_ENTITY.getId())).willReturn(
			expectedUserAlarmStreak
		);
		int updatedConsecutiveDay = consecutiveDay + 1;
		given(commandAlarmResultPort.reflect(expectedUserAlarmStreak)).willReturn(updatedConsecutiveDay);
		given(loadAlarmResultPort.getUser(AUTH_USER_ENTITY.getId())).willReturn(user);
		//when
		sut.add(AUTH_USER_ENTITY.getId(), requestDto);
		//then
		assertThat(expectedUserAlarmStreak.getConsecutiveDays()).isEqualTo(consecutiveDay+1);
		assertThat(user.getMaximumStreak()).isEqualTo(updatedConsecutiveDay);
	}

}