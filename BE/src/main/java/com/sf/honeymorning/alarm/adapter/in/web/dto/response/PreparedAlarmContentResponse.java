package com.sf.honeymorning.alarm.adapter.in.web.dto.response;

import java.time.LocalTime;
import java.util.List;

public record PreparedAlarmContentResponse(
	Integer repeatInterval,
	Integer repeatFrequency,
	LocalTime wakeUpTime,
	String briefingVoiceUrl,
	List<String> quizVoiceUrl
) {

}
