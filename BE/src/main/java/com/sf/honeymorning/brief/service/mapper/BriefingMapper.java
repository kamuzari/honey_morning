package com.sf.honeymorning.brief.service.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.sf.honeymorning.brief.controller.dto.response.BriefHistoryResponseDto;
import com.sf.honeymorning.brief.controller.dto.response.BriefingDetailResponseDto;
import com.sf.honeymorning.brief.controller.dto.response.briefs.MyBriefing;
import com.sf.honeymorning.brief.controller.dto.response.detail.QuizResponseDto;
import com.sf.honeymorning.brief.controller.dto.response.detail.TopicModelWordResponse;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.entity.BriefingTag;
import com.sf.honeymorning.brief.entity.TopicModelWord;
import com.sf.honeymorning.brief.entity.Quiz;

@Component
public class BriefingMapper {

	public BriefHistoryResponseDto toBriefHistoryResponseDto(List<Briefing> briefings,
		Map<Long, List<BriefingTag>> briefCategoryByBrief, Map<Long, List<Quiz>> quizzesByBrief,
		Page<Briefing> briefingPage) {
		return new BriefHistoryResponseDto(briefings.stream()
			.map(brief -> new MyBriefing(brief.getId(), brief.getCreatedAt(),
				briefCategoryByBrief.get(brief.getId()).stream().map(BriefingTag::getWord)
					.toList(),
				brief.getSummaryText(),
				quizzesByBrief.get(brief.getId()).stream()
					.filter(quiz -> quiz.getAnswer().equals(quiz.getSelection()))
					.count())).toList(), briefingPage.getTotalPages());
	}

	public BriefingDetailResponseDto toBriefingDetailResponseDto(Briefing briefing, List<BriefingTag> briefCategories,
		List<Quiz> quizzes, List<TopicModelWord> topicModelWords) {
		return new BriefingDetailResponseDto(
			briefing.getId(),
			briefing.getSummaryText(),
			briefing.getText(),
			briefing.getWakeUpBriefingContent().getFileUrl(),
			topicModelWords.stream()
				.map(topicModelWord -> new TopicModelWordResponse(topicModelWord.getSectionId(),
					topicModelWord.getWord(),
					topicModelWord.getWeight())
				).toList(),
			briefCategories.stream().map(BriefingTag::getWord).toList(),
			quizzes.stream()
				.map(quiz -> new QuizResponseDto(
					quiz.getProblem(),
					quiz.getOption1(),
					quiz.getOption2(),
					quiz.getOption3(),
					quiz.getOption4(),
					quiz.getSelection(),
					quiz.getAnswer()
				)).toList(),
			briefing.getCreatedAt());
	}
}
