package com.sf.honeymorning.alarm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.sf.honeymorning.alarm.dto.request.AlarmSetRequest;
import com.sf.honeymorning.alarm.dto.response.AlarmResponse;
import com.sf.honeymorning.alarm.entity.Alarm;
import com.sf.honeymorning.alarm.entity.DayOfTheWeek;
import com.sf.honeymorning.alarm.exception.AlarmBusinessException;
import com.sf.honeymorning.alarm.repository.AlarmRepository;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.context.MockTestServiceEnvironment;

class AlarmServiceTest extends MockTestServiceEnvironment {

	@InjectMocks
	AlarmService systemUnderTest;

	@Mock
	private AlarmRepository alarmRepository;

	@Test
	@DisplayName("알람 설정 일부문을 변경한다")
	void testSetAlarm() {
		//given
		long alarmId = 1L;
		AlarmSetRequest requestDto = new AlarmSetRequest(
			alarmId,
			LocalTime.now(),
			FAKER_DATE_FACTORY.number().numberBetween(1, 127),
			FAKER_DATE_FACTORY.number().numberBetween(1, 10),
			FAKER_DATE_FACTORY.number().numberBetween(1, 10),
			true
		);

		Alarm previousAlarm = new Alarm(
			alarmId,
			LocalTime.now(),
			FAKER_DATE_FACTORY.number().numberBetween(1, 127),
			FAKER_DATE_FACTORY.number().numberBetween(1, 10),
			FAKER_DATE_FACTORY.number().numberBetween(1, 10),
			true,
			FAKER_DATE_FACTORY.file().fileName()
		);

		given(alarmRepository.findByUserId(AUTH_USER.getId())).willReturn(Optional.of(previousAlarm));

		//when
		systemUnderTest.set(requestDto, AUTH_USER.getId());

		//then
		assertThat(previousAlarm.getRepeatFrequency()).isEqualTo(requestDto.repeatFrequency());
		assertThat(previousAlarm.getRepeatInterval()).isEqualTo(requestDto.repeatInterval());
		assertThat(previousAlarm.isActive()).isEqualTo(requestDto.isActive());
		assertThat(previousAlarm.getDayOfTheWeeks()).isEqualTo(requestDto.weekdays());
		verify(alarmRepository, times(1)).findByUserId(AUTH_USER.getId());
	}

	@Test
	@DisplayName("사용자의 알람 데이터가 없으면 비즈니스 예외가 발생한다")
	void failSetAlarm() {
		//given
		AlarmSetRequest requestDto = new AlarmSetRequest(
			1L,
			LocalTime.now(),
			FAKER_DATE_FACTORY.number().numberBetween(1, 127),
			FAKER_DATE_FACTORY.number().numberBetween(1, 10),
			FAKER_DATE_FACTORY.number().numberBetween(1, 10),
			true
		);

		//when
		//then
		assertThatThrownBy(() -> systemUnderTest.set(requestDto, AUTH_USER.getId()))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("사용자의 알람 데이터가 없으면 비즈니스 예외가 발생한다")
	void testGetMyAlarm() {
		//given
		Alarm expectedMyAlarm = new Alarm(
			1L,
			LocalTime.now(),
			FAKER_DATE_FACTORY.number().numberBetween(1, 127),
			FAKER_DATE_FACTORY.number().numberBetween(1, 10),
			FAKER_DATE_FACTORY.number().numberBetween(1, 10),
			true,
			FAKER_DATE_FACTORY.file().fileName()
		);

		given(alarmRepository.findByUserId(AUTH_USER.getId())).willReturn(Optional.of(expectedMyAlarm));
		//when
		AlarmResponse myAlarm = systemUnderTest.getMyAlarm(AUTH_USER.getId());

		//then
		assertThat(myAlarm).isNotNull();
	}

	@Test
	@DisplayName("슬립모드는 알람시작전 5시간 전이면 가능하다")
	void testCanSleep() {
		//given
		LocalDateTime startAt = LocalDateTime.now();
		Integer everyDay = Arrays.stream(DayOfTheWeek.values()).map(DayOfTheWeek::getShiftedBit)
			.reduce(Integer::sum).orElseThrow();
		Alarm alarm = new Alarm(AUTH_USER.getId(),
			startAt.toLocalTime().plusHours(5),
			everyDay,
			1,
			1,
			true,
			"");
		given(alarmRepository.findByUserIdAndIsActiveTrue(AUTH_USER.getId())).willReturn(Optional.of(alarm));

		//when
		systemUnderTest.verifySleepMode(AUTH_USER.getId(), startAt);
		//then
		verify(alarmRepository, times(1)).findByUserIdAndIsActiveTrue(AUTH_USER.getId());
	}

	@Test
	@DisplayName("슬립모드는 4:59분 이하로 요청하면 실패한다")
	void failCanSleep() {
		//given
		LocalDateTime startAt = LocalDateTime.now();
		Integer everyDay = Arrays.stream(DayOfTheWeek.values()).map(DayOfTheWeek::getShiftedBit)
			.reduce(Integer::sum).orElseThrow();
		Alarm alarm = new Alarm(AUTH_USER.getId(),
			startAt.toLocalTime().plusHours(4).plusMinutes(59),
			everyDay,
			1,
			1,
			true,
			"");
		given(alarmRepository.findByUserIdAndIsActiveTrue(AUTH_USER.getId())).willReturn(Optional.of(alarm));

		//when
		//then
		Assertions.assertThatThrownBy(() -> systemUnderTest.verifySleepMode(AUTH_USER.getId(), startAt))
			.isInstanceOf(AlarmBusinessException.class);
	}

}