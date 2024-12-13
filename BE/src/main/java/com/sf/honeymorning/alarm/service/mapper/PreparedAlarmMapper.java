package com.sf.honeymorning.alarm.service.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.alarm.controller.dto.response.PreparedAlarmContentResponse;
import com.sf.honeymorning.alarm.entity.Alarm;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.quiz.entity.Quiz;

@Component
public class PreparedAlarmMapper {
	public PreparedAlarmContentResponse toPreparedAlarmContentResponse(Alarm alarm, Briefing briefing, List<Quiz> quizzes) {
		return new PreparedAlarmContentResponse(
			alarm.getWakeUpCallPath(),
			alarm.getRepeatInterval(),
			alarm.getRepeatFrequency(),
			alarm.getWakeUpTime(),
			briefing.getVoiceContentUrl(),
			quizzes.stream().map(Quiz::getQuizVoiceUrl).toList()
		);
	}
}
