package com.sf.honeymorning.brief.controller.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.sf.honeymorning.brief.controller.dto.response.detail.QuizResponseDto;
import com.sf.honeymorning.brief.controller.dto.response.detail.TopicModelWordResponse;

public record BriefingDetailResponseDto(
	Long briefId,
	String summaryText,
	String contentText,
	String voiceContentUrl,
	List<TopicModelWordResponse> topicModelResponses,
	List<String> tagNames,
	List<QuizResponseDto> quizResponses,
	LocalDateTime createdAt) {

}
