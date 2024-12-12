package com.sf.honeymorning.alarm.controller.dto.response;

import java.time.LocalTime;
import java.util.List;

public record PreparedAlarmContentResponse(
	String wakeUpCallFilePath,
	Integer repeatInterval,
	Integer repeatFrequency,
	LocalTime wakeUpTime,
	String briefingVoiceUrl,
	List<String> quizVoiceUrl
) {

}
