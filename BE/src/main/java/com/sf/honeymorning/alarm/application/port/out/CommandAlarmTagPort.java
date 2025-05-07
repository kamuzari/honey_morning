package com.sf.honeymorning.alarm.application.port.out;

public interface CommandAlarmTagPort {
	void add(Long userId, String word);
	void remove(Long userId, String word);
}
