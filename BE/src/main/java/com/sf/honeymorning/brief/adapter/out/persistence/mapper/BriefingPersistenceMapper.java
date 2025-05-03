package com.sf.honeymorning.brief.adapter.out.persistence.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.sf.honeymorning.brief.adapter.in.web.dto.response.BriefHistoryResponseDto;
import com.sf.honeymorning.brief.adapter.in.web.dto.response.BriefingDetailResponseDto;
import com.sf.honeymorning.brief.adapter.in.web.dto.response.briefs.MyBriefing;
import com.sf.honeymorning.brief.adapter.in.web.dto.response.detail.QuizResponseDto;
import com.sf.honeymorning.brief.adapter.in.web.dto.response.detail.TopicModelWordResponse;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingTagEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.TopicModelWord;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.QuizEntity;

@Component
public class BriefingPersistenceMapper {

	public BriefHistoryResponseDto toBriefHistoryResponseDto(List<BriefingEntity> briefingEntities,
		Map<Long, List<BriefingTagEntity>> briefCategoryByBrief, Map<Long, List<QuizEntity>> quizzesByBrief,
		Page<BriefingEntity> briefingPage) {
		return new BriefHistoryResponseDto(briefingEntities.stream()
			.map(brief -> new MyBriefing(brief.getId(), brief.getCreatedAt(),
				briefCategoryByBrief.get(brief.getId()).stream().map(BriefingTagEntity::getWord)
					.toList(),
				brief.getSummaryText(),
				quizzesByBrief.get(brief.getId()).stream()
					.filter(quiz -> quiz.getAnswer().equals(quiz.getSelection()))
					.count())).toList(), briefingPage.getTotalPages());
	}

	public BriefingDetailResponseDto toBriefingDetailResponseDto(BriefingEntity briefingEntity, List<BriefingTagEntity> briefCategories,
		List<QuizEntity> quizEntities, List<TopicModelWord> topicModelWords) {
		return new BriefingDetailResponseDto(
			briefingEntity.getId(),
			briefingEntity.getSummaryText(),
			briefingEntity.getText(),
			briefingEntity.getWakeUpBriefingContent().getFileUrl(),
			topicModelWords.stream()
				.map(topicModelWord -> new TopicModelWordResponse(topicModelWord.getSectionId(),
					topicModelWord.getWord(),
					topicModelWord.getWeight())
				).toList(),
			briefCategories.stream().map(BriefingTagEntity::getWord).toList(),
			quizEntities.stream()
				.map(quiz -> new QuizResponseDto(
					quiz.getProblem(),
					quiz.getOption1(),
					quiz.getOption2(),
					quiz.getOption3(),
					quiz.getOption4(),
					quiz.getSelection(),
					quiz.getAnswer()
				)).toList(),
			briefingEntity.getCreatedAt());
	}
}
