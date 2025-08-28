package com.honeymorning.api.alarm.application.port.out;

import com.honeymorning.api.alarm.application.domain.UpdateAlarm;
import com.honeymorning.api.alarm.application.domain.VerifySleepModeAlarm;

public interface LoadAlarmPort {
	UpdateAlarm getAlarm(Long userId);
	VerifySleepModeAlarm getActivatedAlarm(Long userId);
}
