package com.sf.honeymorning.alarm.service.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.alarm.controller.dto.response.PreparedAlarmContentResponse;
import com.sf.honeymorning.alarm.domain.entity.Alarm;
import com.sf.honeymorning.alarm.service.dto.response.AiResponseDto;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingTagEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.TopicModelWord;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.QuizEntity;

@Component
public class AlarmContentMapper {
	public PreparedAlarmContentResponse toPreparedAlarmContentResponse(Alarm alarm,
		BriefingEntity briefingEntity,
		List<QuizEntity> quizEntities) {
		return new PreparedAlarmContentResponse(
			alarm.getRepeatInterval(),
			alarm.getRepeatFrequency(),
			alarm.getWakeUpTime(),
			briefingEntity.getWakeUpBriefingContent().getFileUrl(),
			quizEntities.stream().map(quiz -> quiz.getWakeUpQuizContent().getFileUrl()).toList()
		);
	}

	public BriefingEntity toTotalAlarmContent(AiResponseDto response) {
		return new BriefingEntity(
			response.userId(),
			response.aiBriefings().voiceContent(),
			response.aiBriefings().readContent(),
			response.AiWakeUpCallPath(),
			response.requestTags().stream().map(BriefingTagEntity::new).toList(),
			response.aiQuizzes().stream().map(aiQuizDto ->
				new QuizEntity(aiQuizDto.problem(),
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
