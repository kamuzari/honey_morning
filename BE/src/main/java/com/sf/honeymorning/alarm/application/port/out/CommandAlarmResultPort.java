package com.sf.honeymorning.alarm.application.port.out;

import com.sf.honeymorning.alarm.application.domain.AddAlarmResult;
import com.sf.honeymorning.alarm.application.domain.UpdateStreakUser;
import com.sf.honeymorning.alarm.application.domain.CreateUserAlarmStreak;

public interface CommandAlarmResultPort {

	int reflect(CreateUserAlarmStreak createUserAlarmStreak);

	void reflect(UpdateStreakUser updateStreakUserAlarmStreak);

	void addTodayAlarmResults(AddAlarmResult addAlarmResult);
}
