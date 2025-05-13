package com.sf.honeymorning.alarm.application.port.in.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.sf.honeymorning.alarm.adapter.in.web.dto.request.AddAlarmResultRequestDto;
import com.sf.honeymorning.alarm.application.domain.CreateUserAlarmStreak;
import com.sf.honeymorning.alarm.application.domain.UpdateStreakUser;
import com.sf.honeymorning.alarm.application.port.in.AlarmResultCommandUseCase;
import com.sf.honeymorning.alarm.application.port.out.CommandAlarmResultPort;
import com.sf.honeymorning.alarm.application.port.out.LoadAlarmResultPort;
import com.sf.honeymorning.alarm.application.service.AlarmResultService;
import com.sf.honeymorning.alarm.application.service.mapper.AlarmResultMapper;
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

	@Spy
	AlarmResultMapper alarmResultMapper;

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
		CreateUserAlarmStreak expectedCreateUserAlarmStreak = new CreateUserAlarmStreak(
			AUTH_USER_ENTITY.getId(),
			LocalDateTime.now(),
			initialConsecutiveDay);
		given(loadAlarmResultPort.getUserAlarmResultStreak(AUTH_USER_ENTITY.getId())).willReturn(
			expectedCreateUserAlarmStreak
		);
		given(loadAlarmResultPort.getUser(AUTH_USER_ENTITY.getId())).willReturn(
			new UpdateStreakUser(
				AUTH_USER_ENTITY.getId(),
				initialConsecutiveDay
			));
		//when
		sut.add(AUTH_USER_ENTITY.getId(), requestDto);
		//then
		assertThat(expectedCreateUserAlarmStreak.getConsecutiveDays()).isEqualTo(initialConsecutiveDay + 1);
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
		CreateUserAlarmStreak expectedCreateUserAlarmStreak = new CreateUserAlarmStreak(
			AUTH_USER_ENTITY.getId(),
			LocalDateTime.now().minusDays(2),
			consecutiveDay);
		given(loadAlarmResultPort.getUserAlarmResultStreak(AUTH_USER_ENTITY.getId())).willReturn(
			expectedCreateUserAlarmStreak
		);
		given(loadAlarmResultPort.getUser(AUTH_USER_ENTITY.getId())).willReturn(new UpdateStreakUser(
			AUTH_USER_ENTITY.getId(),
			consecutiveDay
		));
		//when
		sut.add(AUTH_USER_ENTITY.getId(), requestDto);
		//then
		assertThat(expectedCreateUserAlarmStreak.getConsecutiveDays()).isEqualTo(1);
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
		CreateUserAlarmStreak expectedCreateUserAlarmStreak = new CreateUserAlarmStreak(
			AUTH_USER_ENTITY.getId(),
			LocalDateTime.now(),
			consecutiveDay);
		UpdateStreakUser updateStreakUser = new UpdateStreakUser(
			AUTH_USER_ENTITY.getId(),
			consecutiveDay
		);

		given(loadAlarmResultPort.getUserAlarmResultStreak(AUTH_USER_ENTITY.getId())).willReturn(
			expectedCreateUserAlarmStreak
		);
		int updatedConsecutiveDay = consecutiveDay + 1;
		given(commandAlarmResultPort.reflect(expectedCreateUserAlarmStreak)).willReturn(updatedConsecutiveDay);
		given(loadAlarmResultPort.getUser(AUTH_USER_ENTITY.getId())).willReturn(updateStreakUser);
		//when
		sut.add(AUTH_USER_ENTITY.getId(), requestDto);
		//then
		assertThat(expectedCreateUserAlarmStreak.getConsecutiveDays()).isEqualTo(consecutiveDay + 1);
		assertThat(updateStreakUser.getMaximumStreak()).isEqualTo(updatedConsecutiveDay);
	}

}