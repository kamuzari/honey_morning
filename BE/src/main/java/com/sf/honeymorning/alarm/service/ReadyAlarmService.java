package com.sf.honeymorning.alarm.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.alarm.entity.Alarm;
import com.sf.honeymorning.alarm.entity.DayOfWeek;
import com.sf.honeymorning.alarm.repository.AlarmRepository;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.util.TimeUtils;

@Transactional(readOnly = true)
@Service
public class ReadyAlarmService {

	public static final int FIXED_NEXT_MINUTE = 40;
	private final AlarmRepository alarmRepository;
	private final BriefingRepository briefingRepository;

	public ReadyAlarmService(AlarmRepository alarmRepository, BriefingRepository briefingRepository) {
		this.alarmRepository = alarmRepository;
		this.briefingRepository = briefingRepository;
	}

	public List<Alarm> getReadyAlarm() {
		LocalTime timeAfter40Minutes = TimeUtils.getNow().plusMinutes(FIXED_NEXT_MINUTE);
		byte dayOfWeekMask = DayOfWeek.getToday();

		return alarmRepository.findActiveAlarmsForToday(dayOfWeekMask, timeAfter40Minutes);
	}

	@Transactional
	public void bulkSave(Briefing briefing) {
		briefingRepository.save(briefing);
	}

}
