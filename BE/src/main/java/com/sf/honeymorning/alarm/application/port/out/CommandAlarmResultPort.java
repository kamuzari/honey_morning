package com.sf.honeymorning.alarm.application.port.out;

import com.sf.honeymorning.alarm.application.domain.User;
import com.sf.honeymorning.alarm.application.domain.UserAlarmStreak;

public interface CommandAlarmResultPort {

	int reflect(UserAlarmStreak userAlarmStreak);

	void reflect(User userAlarmStreak);

	void addTodayAlarmResults(Long userId, Long briefingId, Integer matchCount);
}
