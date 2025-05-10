package com.sf.honeymorning.alarm.application.port.in.unit;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.sf.honeymorning.alarm.adapter.out.persistence.entity.DayOfTheWeek;
import com.sf.honeymorning.alarm.application.domain.VerifySleepModeAlarm;
import com.sf.honeymorning.alarm.application.port.in.ValidateAlarmUseCase;
import com.sf.honeymorning.alarm.application.port.out.CommandAlarmPort;
import com.sf.honeymorning.alarm.application.port.out.LoadAlarmPort;
import com.sf.honeymorning.alarm.application.service.AlarmService;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.context.mock.MockTest;

class ValidateAlarmUseCaseTest extends MockTest {

	ValidateAlarmUseCase sut;

	@InjectMocks
	AlarmService alarmService;

	@Mock
	LoadAlarmPort loadAlarmPort;

	@Mock
	CommandAlarmPort commandAlarmPort;

	@BeforeEach
	void setUp() {
		this.sut = alarmService;
	}

	@Test
	@DisplayName("슬립모드는 알람시작전 5시간 전이면 가능하다")
	void testCanSleep() {
		//given
		LocalDateTime startAt = LocalDateTime.now();
		Integer everyDay = Arrays.stream(DayOfTheWeek.values()).map(DayOfTheWeek::getShiftedBit)
			.reduce(Integer::sum).orElseThrow();
		var alarm = new VerifySleepModeAlarm(
			startAt.toLocalTime().plusHours(5),
			everyDay,
			true);
		given(loadAlarmPort.getActivatedAlarm(AUTH_USER_ENTITY.getId())).willReturn(alarm);

		//when
		sut.verifySleepMode(AUTH_USER_ENTITY.getId(), startAt);
		//then
		verify(loadAlarmPort, times(1)).getActivatedAlarm(AUTH_USER_ENTITY.getId());
	}

	@Test
	@DisplayName("슬립모드는 4:59분 이하로 요청하면 실패한다")
	void failCanSleep() {
		//given
		LocalDateTime startAt = LocalDateTime.now();
		Integer everyDay = Arrays.stream(DayOfTheWeek.values()).map(DayOfTheWeek::getShiftedBit)
			.reduce(Integer::sum).orElseThrow();
		var alarm = new VerifySleepModeAlarm(
			startAt.toLocalTime().plusHours(4).plusMinutes(59),
			everyDay,
			true);
		given(loadAlarmPort.getActivatedAlarm(AUTH_USER_ENTITY.getId())).willReturn(alarm);

		//when
		//then
		Assertions.assertThatThrownBy(() -> sut.verifySleepMode(AUTH_USER_ENTITY.getId(), startAt))
			.isInstanceOf(BusinessException.class);
	}

}