package com.sf.honeymorning.alarm.integration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import com.sf.honeymorning.alarm.domain.entity.Alarm;
import com.sf.honeymorning.alarm.domain.entity.DayOfTheWeek;
import com.sf.honeymorning.alarm.domain.repository.AlarmRepository;
import com.sf.honeymorning.alarm.service.AlarmContentService;
import com.sf.honeymorning.context.EndPointIntegrationEnvironment;
import com.sf.honeymorning.context.infra.database.MySqlContext;
import com.sf.honeymorning.util.TimeUtils;

class AlarmContentServiceIntegrationTest extends EndPointIntegrationEnvironment implements MySqlContext {

	static final int TOMORROW = LocalDate.now().getDayOfWeek().getValue();

	@Autowired
	AlarmContentService alarmContentService;

	@Autowired
	AlarmRepository alarmRepository;

	int todayScheduledAlarmSize;

	@BeforeEach
	public void setUp() {
		List<Alarm> todayScheduledAlarms = List.of(
			new Alarm(
				3L,
				TimeUtils.getNow().plusMinutes(40),
				DayOfTheWeek.getToday(),
				3,
				3,
				true,
				""
			),
			new Alarm(
				1L,
				TimeUtils.getNow().plusMinutes(40),
				DayOfTheWeek.getToday(),
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

	// @DisplayName("40분후에_시작되는_알람들을_가져온다")
	// @Test
	// void testGetTodayScheduledAlarms() {
	// 	//given
	// 	//when
	// 	List<Alarm> alarms = alarmContentService.getReadyAlarm();
	// 	//then
	// 	Assertions.assertThat(alarms).hasSize(todayScheduledAlarmSize);
	// }
}