package com.sf.honeymorning.alarm.service.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.alarm.controller.dto.response.PreparedAlarmContentResponse;
import com.sf.honeymorning.alarm.domain.entity.Alarm;
import com.sf.honeymorning.alarm.service.dto.response.AiResponseDto;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.entity.BriefingTag;
import com.sf.honeymorning.brief.entity.TopicModelWord;
import com.sf.honeymorning.quiz.entity.Quiz;
import com.sf.honeymorning.tag.entity.Tag;

@Component
public class AlarmContentServiceMapper {
	public PreparedAlarmContentResponse toPreparedAlarmContentResponse(Alarm alarm, Briefing briefing,
		List<Quiz> quizzes) {
		return new PreparedAlarmContentResponse(
			alarm.getWakeUpCallPath(),
			alarm.getRepeatInterval(),
			alarm.getRepeatFrequency(),
			alarm.getWakeUpTime(),
			briefing.getVoiceContentUrl(),
			quizzes.stream().map(Quiz::getQuizVoiceUrl).toList()
		);
	}

	public Briefing toBriefing(AiResponseDto response) {
		return new Briefing(
			response.userId(),
			response.aiBriefings().voiceContent(),
			response.aiBriefings().readContent(),
			"",
			response.requestTags().stream().map(requestTag -> new BriefingTag(new Tag(requestTag))).toList(),
			response.aiQuizzes().stream().map(aiQuizDto ->
				new Quiz(aiQuizDto.problem(),
					aiQuizDto.answer(),
					aiQuizDto.selections(),
					""
				)).toList(),
			response.aiTopics().stream().map(aiTopicDto -> new TopicModelWord(
				aiTopicDto.sectionId(),
				aiTopicDto.word(),
				aiTopicDto.weight())).toList()
		);
	}
}
