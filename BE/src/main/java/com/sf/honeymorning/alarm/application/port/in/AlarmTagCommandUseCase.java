package com.sf.honeymorning.alarm.application.port.in;

public interface AlarmTagCommandUseCase {
	void add(Long userId, String word);

	void remove(Long userId, String word);
}
