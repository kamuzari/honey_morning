package com.honeymorning.batch.utils;

import java.time.LocalTime;

public class TimeUtils {
	public static LocalTime getNow() {
		return LocalTime.now().withSecond(0).withNano(0);
	}
}
