package com.honeymorning.api.alarm.application.port.out;

import com.honeymorning.api.alarm.application.domain.UpdateStreakUser;
import com.honeymorning.api.alarm.application.domain.CreateUserAlarmStreak;

public interface LoadAlarmResultPort {
	CreateUserAlarmStreak getUserAlarmResultStreak(Long userId);
	UpdateStreakUser getUser(Long userId);
}
