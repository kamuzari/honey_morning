package com.sf.honeymorning.alarm.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.javafaker.Faker;

class AlarmTest {
	static final Faker DATE_GENERATOR = new Faker();

	@Test
	@DisplayName("사용자가 회원가입을 하면 초기 알람셋팅을 함께 생성한다")
	void testInitalize() {
		//given
		//when
		Alarm alarm = Alarm.initialize(DATE_GENERATOR.number().randomNumber());
		//then
		assertThat(alarm).isNotNull();
		assertThat(alarm.isActive()).isFalse();
	}

	@Test
	@DisplayName("사용자가 알람 설정을 변경한다")
	void testUpdate() {
		//given
		Alarm existedAlarm = Alarm.initialize(DATE_GENERATOR.number().randomNumber());
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
	@DisplayName("수면모드는 알람설정 시간과 지금 현재시간에서 5시간 이상이여야 가능하다")
	void testCanSleepMode() {
		//given
		Alarm existedAlarm = Alarm.initialize(DATE_GENERATOR.number().randomNumber());
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
		long userId = DATE_GENERATOR.number().randomNumber();
		LocalTime wakeUpTime = LocalTime.now().plusHours(5);
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
		Alarm alarm = new Alarm(
			userId,
			wakeUpTime,
			dayOfTheWeeks,
			repeatFrequency,
			repeatInterval,
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
		long userId = DATE_GENERATOR.number().randomNumber();
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
		Alarm alarm = new Alarm(
			userId,
			wakeUpTime,
			dayOfTheWeeks,
			repeatFrequency,
			repeatInterval,
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
		long userId = DATE_GENERATOR.number().randomNumber();
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
		Alarm alarm = new Alarm(
			userId,
			wakeUpTime,
			dayOfTheWeeks,
			repeatFrequency,
			repeatInterval,
			isActive
		);

		//when
		LocalDateTime requestTime = LocalDateTime.now();
		boolean canSleepMode = alarm.canSleepMode(requestTime);
		//then
		assertThat(canSleepMode).isFalse();
	}
}