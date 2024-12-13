package com.sf.honeymorning.brief.service;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sf.honeymorning.brief.controller.dto.response.detail.TopicModelWordResponse;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.entity.TopicModelWord;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.brief.repository.TopicModelWordRepository;
import com.sf.honeymorning.common.exception.model.ErrorProtocol;
import com.sf.honeymorning.common.exception.model.NotFoundResourceException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TopicModelService {
	private final TopicModelWordRepository topicModelWordRepository;
	private final BriefingRepository briefingRepository;

	public List<TopicModelWordResponse> getTopicModel(Long briefId) {
		Briefing briefing = briefingRepository.findById(briefId)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("존재하지 않는 브리핑입니다 briefing id : {0}", briefId),
				ErrorProtocol.BUSINESS_VIOLATION));
		List<TopicModelWord> topicModelWords = topicModelWordRepository.findByBriefing(briefing);

		return topicModelWords.stream()
			.map(topicModelWord -> new TopicModelWordResponse(topicModelWord.getSectionId(),
				topicModelWord.getWord(),
				topicModelWord.getWeight())
			).toList();
	}
}
