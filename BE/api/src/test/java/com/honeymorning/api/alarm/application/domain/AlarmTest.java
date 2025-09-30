package com.honeymorning.api.alarm.application.domain;

import static com.honeymorning.api.brief.utils.BriefingMockGenerator.GENERATOR;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.honeymorning.common.domain.alarm.entity.DayOfTheWeek;

class AlarmTest {

	@Test
	@DisplayName("수면모드는 알람설정 시간과 지금 현재시간에서 5시간 이상이여야 가능하다")
	void testCanSleepMode() {
		//given
		UpdateAlarm existedAlarm = UpdateAlarm.initialize(GENERATOR.number().randomNumber());
		LocalTime wakeUpTime = LocalTime.now();
		Integer repeatFrequency = 3;
		Integer repeatInterval = 3;
		boolean isActive = true;
		Integer dayOfTheWeeks = DayOfTheWeek.toBit(DayOfTheWeek.MONDAY,
			DayOfTheWeek.TUESDAY,
			DayOfTheWeek.THURSDAY,
			DayOfTheWeek.FRIDAY);

		//when
		existedAlarm.update(
			wakeUpTime,
			dayOfTheWeeks,
			repeatFrequency,
			repeatInterval,
			isActive
		);

		//then
		assertThat(existedAlarm.isActive()).isTrue();
		assertThat(existedAlarm.getRepeatFrequency()).isEqualTo(repeatFrequency);
		assertThat(existedAlarm.getRepeatInterval()).isEqualTo(repeatInterval);
		assertThat(existedAlarm.getWakeUpTime()).isEqualTo(wakeUpTime);
		assertThat(existedAlarm.getDayOfTheWeeks()).isEqualTo(dayOfTheWeeks);
	}

	@Test
	@DisplayName("슬립모드는 알람 시작 시간 5시간 보다 많아야 가능하다")
	void testSleepMode() {
		//given
		LocalTime wakeUpTime = LocalTime.now().plusHours(5);
		boolean isActive = true;
		Integer dayOfTheWeeks = DayOfTheWeek.toBit(DayOfTheWeek.MONDAY,
			DayOfTheWeek.TUESDAY,
			DayOfTheWeek.WEDNESDAY,
			DayOfTheWeek.THURSDAY,
			DayOfTheWeek.FRIDAY,
			DayOfTheWeek.SATURDAY,
			DayOfTheWeek.SUNDAY
		);

		var alarm = new VerifySleepModeAlarm(
			wakeUpTime,
			dayOfTheWeeks,
			isActive
		);

		//when
		LocalDateTime requestTime = LocalDateTime.now();
		boolean canSleepMode = alarm.canSleepMode(requestTime);
		//then
		assertThat(canSleepMode).isTrue();
	}

	@Test
	@DisplayName("슬립모드는 알람이 활성화되지 않으면 불가능하다")
	void failSleepModeUnActiveMode() {
		//given
		long userId = GENERATOR.number().randomNumber();
		LocalTime wakeUpTime = LocalTime.now().plusHours(5).minusMinutes(1);
		Integer repeatFrequency = 3;
		Integer repeatInterval = 3;
		boolean isActive = false;
		Integer dayOfTheWeeks = DayOfTheWeek.toBit(DayOfTheWeek.MONDAY,
			DayOfTheWeek.TUESDAY,
			DayOfTheWeek.WEDNESDAY,
			DayOfTheWeek.THURSDAY,
			DayOfTheWeek.FRIDAY,
			DayOfTheWeek.SATURDAY,
			DayOfTheWeek.SUNDAY
		);
		var alarm = new VerifySleepModeAlarm(
			wakeUpTime,
			dayOfTheWeeks,
			isActive
		);

		//when
		LocalDateTime requestTime = LocalDateTime.now();
		boolean canSleepMode = alarm.canSleepMode(requestTime);
		//then
		assertThat(canSleepMode).isFalse();
	}

	@Test
	@DisplayName("슬립모드는 알람 시작 시간 5시간 보다 적으면 불가능하다")
	void failSleepMode() {
		//given
		long userId = GENERATOR.number().randomNumber();
		LocalTime wakeUpTime = LocalTime.now().plusHours(5).minusMinutes(1);
		Integer repeatFrequency = 3;
		Integer repeatInterval = 3;
		boolean isActive = true;
		Integer dayOfTheWeeks = DayOfTheWeek.toBit(DayOfTheWeek.MONDAY,
			DayOfTheWeek.TUESDAY,
			DayOfTheWeek.WEDNESDAY,
			DayOfTheWeek.THURSDAY,
			DayOfTheWeek.FRIDAY,
			DayOfTheWeek.SATURDAY,
			DayOfTheWeek.SUNDAY
		);
		var alarm = new VerifySleepModeAlarm(
			wakeUpTime,
			dayOfTheWeeks,
			isActive
		);

		//when
		LocalDateTime requestTime = LocalDateTime.now();
		boolean canSleepMode = alarm.canSleepMode(requestTime);
		//then
		assertThat(canSleepMode).isFalse();
	}

}