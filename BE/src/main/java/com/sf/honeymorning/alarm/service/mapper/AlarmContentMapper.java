package com.sf.honeymorning.alarm.service.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.alarm.controller.dto.response.PreparedAlarmContentResponse;
import com.sf.honeymorning.alarm.domain.entity.Alarm;
import com.sf.honeymorning.alarm.service.dto.response.AiResponseDto;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.entity.BriefingTag;
import com.sf.honeymorning.brief.entity.TopicModelWord;
import com.sf.honeymorning.brief.entity.Quiz;

@Component
public class AlarmContentMapper {
	public PreparedAlarmContentResponse toPreparedAlarmContentResponse(Alarm alarm,
		Briefing briefing,
		List<Quiz> quizzes) {
		return new PreparedAlarmContentResponse(
			alarm.getRepeatInterval(),
			alarm.getRepeatFrequency(),
			alarm.getWakeUpTime(),
			briefing.getWakeUpBriefingContent().getFileUrl(),
			quizzes.stream().map(quiz -> quiz.getWakeUpQuizContent().getFileUrl()).toList()
		);
	}

	public Briefing toTotalAlarmContent(AiResponseDto response) {
		return new Briefing(
			response.userId(),
			response.aiBriefings().voiceContent(),
			response.aiBriefings().readContent(),
			response.AiWakeUpCallPath(),
			response.requestTags().stream().map(BriefingTag::new).toList(),
			response.aiQuizzes().stream().map(aiQuizDto ->
				new Quiz(aiQuizDto.problem(),
					aiQuizDto.answer(),
					aiQuizDto.selections()
				)).toList(),
			response.aiTopics().stream().map(aiTopicDto -> new TopicModelWord(
				aiTopicDto.sectionId(),
				aiTopicDto.word(),
				aiTopicDto.weight())).toList()
		);
	}
}
