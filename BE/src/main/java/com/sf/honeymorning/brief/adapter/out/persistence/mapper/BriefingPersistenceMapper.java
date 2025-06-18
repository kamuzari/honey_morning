package com.sf.honeymorning.brief.adapter.out.persistence.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.sf.honeymorning.alarm.application.service.dto.response.AiResponseDto;
import com.sf.honeymorning.brief.adapter.in.web.dto.response.BriefHistoryResponseDto;
import com.sf.honeymorning.brief.adapter.in.web.dto.response.BriefingDetailResponseDto;
import com.sf.honeymorning.brief.adapter.in.web.dto.response.briefs.MyBriefing;
import com.sf.honeymorning.brief.adapter.in.web.dto.response.detail.QuizResponseDto;
import com.sf.honeymorning.brief.adapter.in.web.dto.response.detail.TopicModelWordResponse;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingTagEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.QuizEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.TopicModelWordEntity;
import com.sf.honeymorning.brief.application.domain.TextToSpeechContent;

@Component
public class BriefingPersistenceMapper {

	public BriefHistoryResponseDto toBriefHistoryResponseDto(
		List<BriefingEntity> briefingEntities,
		Map<Long, List<BriefingTagEntity>> briefCategoryByBrief,
		Map<Long, List<QuizEntity>> quizzesByBrief,
		Page<BriefingEntity> briefingPage) {

		return new BriefHistoryResponseDto(briefingEntities.stream()
			.map(brief ->
				new MyBriefing(
					brief.getId(),
					brief.getCreatedAt(),
					briefCategoryByBrief.get(brief.getId()).stream()
						.map(BriefingTagEntity::getWord)
						.toList(),
					brief.getSummaryText(),
					quizzesByBrief.get(brief.getId()).stream()
						.filter(quiz -> quiz.getAnswer().equals(quiz.getSelection()))
						.count())).toList(), briefingPage.getTotalPages());
	}

	public BriefingDetailResponseDto toBriefingDetailResponseDto(
		BriefingEntity briefingEntity,
		List<BriefingTagEntity> briefCategories,
		List<QuizEntity> quizEntities,
		List<TopicModelWordEntity> topicModelWordEntities) {

		return new BriefingDetailResponseDto(
			briefingEntity.getId(),
			briefingEntity.getSummaryText(),
			briefingEntity.getText(),
			briefingEntity.getWakeUpBriefingContent().getFileUrl(),
			topicModelWordEntities.stream()
				.map(topicModelWord ->
					new TopicModelWordResponse(
						topicModelWord.getSectionId(),
						topicModelWord.getWord(),
						topicModelWord.getWeight())
				).toList(),
			briefCategories.stream().map(BriefingTagEntity::getWord).toList(),
			quizEntities.stream()
				.map(quiz ->
					new QuizResponseDto(
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
			response.aiTopics().stream().map(aiTopicDto -> new TopicModelWordEntity(
				aiTopicDto.sectionId(),
				aiTopicDto.word(),
				aiTopicDto.weight())).toList()
		);
	}

	public TextToSpeechContent toTextToSpeechContent(BriefingEntity briefingEntity) {
		return new TextToSpeechContent(
			briefingEntity.getId(), briefingEntity.getSummaryText(),
			briefingEntity.getQuizEntities().stream()
				.map(
					quizEntity -> new TextToSpeechContent.textToSpeechQuiz(quizEntity.getId(), quizEntity.getProblem()))
				.toList()
		);
	}
}
