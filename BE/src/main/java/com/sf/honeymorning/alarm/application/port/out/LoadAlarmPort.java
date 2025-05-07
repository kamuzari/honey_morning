package com.sf.honeymorning.alarm.application.port.out;

import com.sf.honeymorning.alarm.application.domain.UpdateAlarm;
import com.sf.honeymorning.alarm.application.domain.VerifySleepModeAlarm;

public interface LoadAlarmPort {
	UpdateAlarm getAlarm(Long userId);
	VerifySleepModeAlarm getActivatedAlarm(Long userId);
}
