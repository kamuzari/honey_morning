package com.honeymorning.api.alarm.adapter.out.persistence.entity;

import static com.honeymorning.api.brief.utils.BriefingMockGenerator.GENERATOR;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.honeymorning.common.domain.alarm.entity.AlarmEntity;
import com.honeymorning.common.domain.alarm.entity.DayOfTheWeek;

class AlarmEntityTest {

	@Test
	@DisplayName("사용자가 회원가입을 하면 초기 알람셋팅을 함께 생성한다")
	void testInitalize() {
		//given
		//when
		AlarmEntity alarmEntity = AlarmEntity.initialize(GENERATOR.number().randomNumber());
		//then
		assertThat(alarmEntity).isNotNull();
		assertThat(alarmEntity.isActive()).isFalse();
	}

	@Test
	@DisplayName("사용자가 알람 설정을 변경한다")
	void testUpdate() {
		//given
		AlarmEntity existedAlarmEntity = AlarmEntity.initialize(GENERATOR.number().randomNumber());
		LocalTime wakeUpTime = LocalTime.now();
		Integer repeatFrequency = 3;
		Integer repeatInterval = 3;
		boolean isActive = true;
		Integer dayOfTheWeeks = DayOfTheWeek.toBit(DayOfTheWeek.MONDAY,
			DayOfTheWeek.TUESDAY,
			DayOfTheWeek.THURSDAY,
			DayOfTheWeek.FRIDAY);

		//when
		existedAlarmEntity.update(
			wakeUpTime,
			dayOfTheWeeks,
			repeatFrequency,
			repeatInterval,
			isActive
		);

		//then
		assertThat(existedAlarmEntity.isActive()).isTrue();
		assertThat(existedAlarmEntity.getRepeatFrequency()).isEqualTo(repeatFrequency);
		assertThat(existedAlarmEntity.getRepeatInterval()).isEqualTo(repeatInterval);
		assertThat(existedAlarmEntity.getWakeUpTime()).isEqualTo(wakeUpTime);
		assertThat(existedAlarmEntity.getDayOfTheWeeks()).isEqualTo(dayOfTheWeeks);
	}

	@Test
	@DisplayName("수면모드는 알람설정 시간과 지금 현재시간에서 5시간 이상이여야 가능하다")
	void testCanSleepMode() {
		//given
		AlarmEntity existedAlarmEntity = AlarmEntity.initialize(GENERATOR.number().randomNumber());
		LocalTime wakeUpTime = LocalTime.now();
		Integer repeatFrequency = 3;
		Integer repeatInterval = 3;
		boolean isActive = true;
		Integer dayOfTheWeeks = DayOfTheWeek.toBit(DayOfTheWeek.MONDAY,
			DayOfTheWeek.TUESDAY,
			DayOfTheWeek.THURSDAY,
			DayOfTheWeek.FRIDAY);

		//when
		existedAlarmEntity.update(
			wakeUpTime,
			dayOfTheWeeks,
			repeatFrequency,
			repeatInterval,
			isActive
		);

		//then
		assertThat(existedAlarmEntity.isActive()).isTrue();
		assertThat(existedAlarmEntity.getRepeatFrequency()).isEqualTo(repeatFrequency);
		assertThat(existedAlarmEntity.getRepeatInterval()).isEqualTo(repeatInterval);
		assertThat(existedAlarmEntity.getWakeUpTime()).isEqualTo(wakeUpTime);
		assertThat(existedAlarmEntity.getDayOfTheWeeks()).isEqualTo(dayOfTheWeeks);
	}
}