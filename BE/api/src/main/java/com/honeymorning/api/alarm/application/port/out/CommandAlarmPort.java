package com.honeymorning.api.alarm.application.port.out;

import com.honeymorning.api.alarm.application.domain.UpdateAlarm;

public interface CommandAlarmPort {
	void reflect(UpdateAlarm updateAlarm);
}
