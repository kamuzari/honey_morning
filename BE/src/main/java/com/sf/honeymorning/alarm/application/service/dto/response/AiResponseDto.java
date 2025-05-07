package com.sf.honeymorning.alarm.application.service.dto.response;

import java.util.List;

import com.sf.honeymorning.brief.common.TopicWordConstraint;
import com.sf.honeymorning.brief.common.QuizConstraint;

public record AiResponseDto(
	Long userId,
	AiBriefingDto aiBriefings,
	List<AiQuizDto> aiQuizzes,
	List<AiTopicDto> aiTopics,
	List<String> requestTags,
	String AiWakeUpCallPath) {

	public AiResponseDto {
		if (aiTopics.size() != TopicWordConstraint.TOPIC_WORD_TOTAL_SIZE) {
			throw new IllegalArgumentException("AI 토픽 모델링 규약에 위반하였습니다.");
		}

		if (aiQuizzes.size() != QuizConstraint.TOTAL_QUIZ_SIZE) {
			throw new IllegalArgumentException("AI 퀴즈 규약에 위반하였습니다.");
		}
	}
}

