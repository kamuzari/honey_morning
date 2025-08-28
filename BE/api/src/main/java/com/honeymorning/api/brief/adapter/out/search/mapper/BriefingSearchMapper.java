package com.honeymorning.api.brief.adapter.out.search.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.honeymorning.api.brief.adapter.out.persistence.entity.BriefingEntity;
import com.honeymorning.api.brief.adapter.out.persistence.entity.TopicModelWordEntity;
import com.honeymorning.api.brief.adapter.out.search.index.BriefingContentDocument;

@Component
public class BriefingSearchMapper {

	public BriefingContentDocument toDocument(BriefingEntity briefingEntity) {
		return new BriefingContentDocument(
			briefingEntity.getId(),
			briefingEntity.getUserId(),
			briefingEntity.getSummaryText(),
			briefingEntity.getText(),
			briefingEntity.getTopicModelWordEntities().stream().map(TopicModelWordEntity::getWord).toList(),
			briefingEntity.getQuizEntities().stream()
				.map(quiz -> new BriefingContentDocument.QuizDocument(
					quiz.getId(),
					quiz.getProblem(),
					List.of(quiz.getOption1(), quiz.getOption2(), quiz.getOption3(), quiz.getOption4())
				)).toList()
		);
	}

}
