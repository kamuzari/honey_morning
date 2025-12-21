package com.honeymorning.api.alarm.application.port.in;

import java.time.LocalDateTime;

public interface ValidateAlarmUseCase {
	void verifySleepMode(Long userId, LocalDateTime sleepStartAt);
}
