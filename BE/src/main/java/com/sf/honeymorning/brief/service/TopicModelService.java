package com.sf.honeymorning.brief.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.entity.TopicModel;
import com.sf.honeymorning.brief.entity.TopicModelWord;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.brief.repository.TopicModelRepository;
import com.sf.honeymorning.brief.repository.TopicModelWordRepository;
import com.sf.honeymorning.brief.repository.WordRepository;
import com.sf.honeymorning.brief.controller.dto.response.detail.TopicModelWordResponse;
import com.sf.honeymorning.brief.controller.dto.response.detail.WordResponseDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TopicModelService {
	private final TopicModelRepository topicModelRepository;
	private final BriefingRepository briefingRepository;
	private final WordRepository wordRepository;
	private final TopicModelWordRepository topicModelWordRepository;

	// 프론트엔드에서 요청을 했을 때 가지고 있는 topicModeling 정보를 전달하는 메서드
	public List<TopicModelWordResponse> getTopicModel(Long briefId) {
		Briefing briefing = briefingRepository.findById(briefId)
			.orElseThrow(() -> new RuntimeException("Brief not found"));
		List<TopicModel> topicModel = topicModelRepository.findByBriefing(briefing);

		List<TopicModelWordResponse> topicModelWordResponseList = new ArrayList<>();

		// topicModelWord를 순회하면서 dto list에 삽입
		for (int i = 0; i < topicModel.size(); i++) {
			TopicModel tm = topicModel.get(i);

			List<TopicModelWord> topicModelWordList = topicModelWordRepository.findByTopicModel(tm);
			List<WordResponseDto> wordResponseDtoList = new ArrayList<>();

			for (TopicModelWord tmw : topicModelWordList) {
				WordResponseDto wordResponseDto = WordResponseDto.builder()
					.word(tmw.getWord().getWord())
					.weight(tmw.getWeight())
					.build();

				wordResponseDtoList.add(wordResponseDto);
			}
			TopicModelWordResponse topicModelWordResponse =
				TopicModelWordResponse.builder().id(tm.getSectionId())
				.words(wordResponseDtoList)
				.build();

			topicModelWordResponseList.add(topicModelWordResponse);
		}

		return topicModelWordResponseList;
	}
}
