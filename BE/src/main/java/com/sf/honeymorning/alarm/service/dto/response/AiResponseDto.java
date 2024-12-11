package com.sf.honeymorning.alarm.service.dto.response;

import java.util.List;

public record AiResponseDto(
	Long userId,
	AiBriefingDto aiBriefings,
	List<AiQuizDto> aiQuizzes,
	List<AiTopicDto> aiTopics,
	List<String> requestTags,
	String AiMorningCallPath) {

	private static final int AI_TOPIC_SIZE = 180;
	private static final int AI_QUIZ_SIZE = 2;

	public AiResponseDto {
		if (aiTopics.size() != AI_TOPIC_SIZE) {
			throw new IllegalArgumentException("AI 토픽 모델링 규약에 위반하였습니다.");
		}

		if (aiQuizzes.size() != AI_QUIZ_SIZE) {
			throw new IllegalArgumentException("AI 퀴즈 규약에 위반하였습니다.");
		}
	}
}

