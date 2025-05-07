package com.sf.honeymorning.alarm.application.port.out;

import com.sf.honeymorning.alarm.adapter.out.persistence.entity.UserAlarmResultStreakEntity;
import com.sf.honeymorning.alarm.application.domain.User;
import com.sf.honeymorning.alarm.application.domain.UserAlarmStreak;

public interface LoadAlarmResultPort {
	UserAlarmStreak getUserAlarmResultStreak(Long userId);
	User getUser(Long userId);
}
