package com.honeymorning.api.alarm.application.port.out;

public interface ValidationAlarmTagPort {
	void validate(Long userId, String word);
}
