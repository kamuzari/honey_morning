package com.sf.honeymorning.alarm.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sf.honeymorning.alarm.entity.Alarm;
import com.sf.honeymorning.alarm.entity.DayOfWeek;
import com.sf.honeymorning.alarm.repository.AlarmRepository;
import com.sf.honeymorning.context.IntegrationEnvironment;
import com.sf.honeymorning.context.database.MySqlContext;
import com.sf.honeymorning.util.TimeUtils;

class ReadyAlarmServiceTest extends IntegrationEnvironment implements MySqlContext {

	static final byte TOMORROW = (byte)(LocalDate.now().getDayOfWeek().getValue());

	@Autowired
	ReadyAlarmService readyAlarmService;

	@Autowired
	AlarmRepository alarmRepository;

	int todayScheduledAlarmSize;

	@BeforeEach
	public void setUp() {
		List<Alarm> todayScheduledAlarms = List.of(
			new Alarm(
				3L,
				TimeUtils.getNow().plusMinutes(40),
				DayOfWeek.getToday(),
				3,
				3,
				true,
				""
			),
			new Alarm(
				1L,
				TimeUtils.getNow().plusMinutes(40),
				DayOfWeek.getToday(),
				3,
				3,
				true,
				""
			)
		);
		ArrayList totalAlarms = new ArrayList<>(todayScheduledAlarms);
		totalAlarms.add(new Alarm(
			2L,
			TimeUtils.getNow().plusMinutes(39),
			TOMORROW,
			3,
			3,
			true,
			""
		));

		alarmRepository.saveAll(totalAlarms);
		todayScheduledAlarmSize = todayScheduledAlarms.size();
	}

	@DisplayName("40분후에_시작되는_알람들을_가져온다")
	@Test
	void testGetTodayScheduledAlarms() {
		//given
		//when
		List<Alarm> alarms = readyAlarmService.getReadyAlarm();
		//then
		Assertions.assertThat(alarms).hasSize(todayScheduledAlarmSize);
	}
}