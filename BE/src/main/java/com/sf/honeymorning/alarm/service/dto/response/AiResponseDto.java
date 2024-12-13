package com.sf.honeymorning.alarm.service.dto.response;

import java.util.List;

import com.sf.honeymorning.brief.entity.violation.QuizViolation;
import com.sf.honeymorning.brief.entity.violation.TopicWordViolation;

public record AiResponseDto(
	Long userId,
	AiBriefingDto aiBriefings,
	List<AiQuizDto> aiQuizzes,
	List<AiTopicDto> aiTopics,
	List<String> requestTags,
	String AiWakeUpCallPath) {


	public AiResponseDto {
		if (aiTopics.size() != TopicWordViolation.TOPIC_WORD_TOTAL_SIZE) {
			throw new IllegalArgumentException("AI 토픽 모델링 규약에 위반하였습니다.");
		}

		if (aiQuizzes.size() != QuizViolation.TOTAL_OF_COUNT) {
			throw new IllegalArgumentException("AI 퀴즈 규약에 위반하였습니다.");
		}
	}
}

