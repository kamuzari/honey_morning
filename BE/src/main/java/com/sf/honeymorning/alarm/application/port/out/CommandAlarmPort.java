package com.sf.honeymorning.alarm.application.port.out;

import com.sf.honeymorning.alarm.application.domain.UpdateAlarm;

public interface CommandAlarmPort {
	void reflect(UpdateAlarm updateAlarm);
}
