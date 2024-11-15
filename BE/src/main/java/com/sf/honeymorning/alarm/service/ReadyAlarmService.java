package com.sf.honeymorning.alarm.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.alarm.entity.Alarm;
import com.sf.honeymorning.alarm.entity.DayOfWeek;
import com.sf.honeymorning.alarm.repository.AlarmRepository;
import com.sf.honeymorning.util.TimeUtils;

@Transactional(readOnly = true)
@Service
public class ReadyAlarmService {

	public static final int FIXED_NEXT_MINUTE = 40;
	private final AlarmRepository alarmRepository;

	public ReadyAlarmService(AlarmRepository alarmRepository) {
		this.alarmRepository = alarmRepository;
	}

	// todo: 이게 1만개라면 어쩔건데? 그 이상이면 메모리 터져 임마!
	public List<Alarm> getReadyAlarm() {
		LocalTime timeAfter40Minutes = TimeUtils.getNow().plusMinutes(FIXED_NEXT_MINUTE);
		byte dayOfWeekMask = DayOfWeek.getToday();

		return alarmRepository.findActiveAlarmsForToday(dayOfWeekMask, timeAfter40Minutes);
	}

}
