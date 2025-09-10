package com.honeymorning.api.alarm.application.port.out;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.honeymorning.api.alarm.adapter.out.persistence.AlarmPersistenceAdapter;
import com.honeymorning.api.alarm.adapter.out.persistence.repository.AlarmRepository;
import com.honeymorning.api.context.mock.MockTest;
import com.honeymorning.common.exception.NotFoundResourceException;

class LoadAlarmPortTest extends MockTest {
	LoadAlarmPort sut;

	@InjectMocks
	AlarmPersistenceAdapter alarmPersistenceAdapter;

	@Mock
	AlarmRepository alarmRepository;

	@BeforeEach
	void setUp() {
		this.sut = alarmPersistenceAdapter;
	}

	@Test
	@DisplayName("사용자의 알람 데이터가 없으면 비즈니스 예외가 발생한다")
	void failUpdateAlarm() {
		//given
		Long alarmId = 1L;

		//when
		//then
		assertThatThrownBy(() -> sut.getAlarm(alarmId))
			.isInstanceOf(NotFoundResourceException.class);
	}
}