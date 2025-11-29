package com.honeymorning.api.alarm.application.port.out;

import com.honeymorning.api.alarm.application.domain.CreateUserAlarmStreak;
import com.honeymorning.api.alarm.application.domain.UpdateStreakUser;

public interface LoadAlarmResultPort {
	CreateUserAlarmStreak getUserAlarmResultStreak(Long userId);

	UpdateStreakUser getUser(Long userId);
}
