package com.honeymorning.api.alarm.application.port.in.unit;

import static com.honeymorning.api.brief.utils.BriefingMockGenerator.GENERATOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.sf.honeymorning.alarm.adapter.in.web.dto.request.AlarmSetRequest;
import com.sf.honeymorning.alarm.application.domain.UpdateAlarm;
import com.sf.honeymorning.alarm.application.port.in.AlarmCommandUseCase;
import com.sf.honeymorning.alarm.application.port.out.CommandAlarmPort;
import com.sf.honeymorning.alarm.application.port.out.LoadAlarmPort;
import com.sf.honeymorning.alarm.application.service.AlarmService;
import com.honeymorning.api.context.mock.MockTest;

class AlarmCommandUseCaseTest extends MockTest {
	AlarmCommandUseCase sut;

	@InjectMocks
	AlarmService alarmService;

	@Mock
	LoadAlarmPort loadAlarmPort;

	@Mock
	CommandAlarmPort commandAlarmPort;

	@BeforeEach
	void setUp() {
		this.sut=alarmService;
	}

	@Test
	@DisplayName("알람 설정 일부문을 변경한다")
	void testUpdateAlarm() {
		//given
		long alarmId = 1L;
		AlarmSetRequest requestDto = new AlarmSetRequest(
			alarmId,
			LocalTime.now(),
			GENERATOR.number().numberBetween(1, 127),
			GENERATOR.number().numberBetween(1, 10),
			GENERATOR.number().numberBetween(1, 10),
			true
		);

		UpdateAlarm previousUpdateAlarm = new UpdateAlarm(
			1L,
			alarmId,
			LocalTime.now(),
			GENERATOR.number().numberBetween(1, 127),
			GENERATOR.number().numberBetween(1, 10),
			GENERATOR.number().numberBetween(1, 10),
			true
		);

		given(loadAlarmPort.getAlarm(AUTH_USER_ENTITY.getId())).willReturn(previousUpdateAlarm);

		//when
		sut.update(requestDto, AUTH_USER_ENTITY.getId());

		//then
		assertThat(previousUpdateAlarm.getRepeatFrequency()).isEqualTo(requestDto.repeatFrequency());
		assertThat(previousUpdateAlarm.getRepeatInterval()).isEqualTo(requestDto.repeatInterval());
		assertThat(previousUpdateAlarm.isActive()).isEqualTo(requestDto.isActive());
		assertThat(previousUpdateAlarm.getDayOfTheWeeks()).isEqualTo(requestDto.weekdays());
		verify(loadAlarmPort, times(1)).getAlarm(AUTH_USER_ENTITY.getId());
	}

}