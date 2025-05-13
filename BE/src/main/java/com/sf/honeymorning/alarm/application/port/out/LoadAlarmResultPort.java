package com.sf.honeymorning.alarm.application.port.out;

import com.sf.honeymorning.alarm.application.domain.UpdateStreakUser;
import com.sf.honeymorning.alarm.application.domain.CreateUserAlarmStreak;

public interface LoadAlarmResultPort {
	CreateUserAlarmStreak getUserAlarmResultStreak(Long userId);
	UpdateStreakUser getUser(Long userId);
}
