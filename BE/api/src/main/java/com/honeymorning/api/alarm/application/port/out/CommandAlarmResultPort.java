package com.honeymorning.api.alarm.application.port.out;

import com.honeymorning.api.alarm.application.domain.AddAlarmResult;
import com.honeymorning.api.alarm.application.domain.UpdateStreakUser;
import com.honeymorning.api.alarm.application.domain.CreateUserAlarmStreak;

public interface CommandAlarmResultPort {

	int reflect(CreateUserAlarmStreak createUserAlarmStreak);

	void reflect(UpdateStreakUser updateStreakUserAlarmStreak);

	void addTodayAlarmResults(AddAlarmResult addAlarmResult);
}
