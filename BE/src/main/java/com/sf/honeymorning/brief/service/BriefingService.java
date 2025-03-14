package com.sf.honeymorning.brief.service;

import static com.sf.honeymorning.common.exception.model.constant.ErrorProtocol.POLICY_VIOLATION;
import static java.text.MessageFormat.format;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.brief.controller.dto.response.BriefHistoryResponseDto;
import com.sf.honeymorning.brief.controller.dto.response.BriefingDetailResponseDto;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.entity.BriefingTag;
import com.sf.honeymorning.brief.entity.Quiz;
import com.sf.honeymorning.brief.entity.TopicModelWord;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.brief.repository.BriefingTagRepository;
import com.sf.honeymorning.brief.repository.QuizRepository;
import com.sf.honeymorning.brief.repository.TopicModelWordRepository;
import com.sf.honeymorning.brief.service.mapper.BriefingMapper;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.common.exception.model.NotFoundResourceException;
import com.sf.honeymorning.common.exception.model.constant.ErrorProtocol;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(readOnly = true)
@Service
public class BriefingService {

	private final BriefingRepository briefingRepository;
	private final BriefingTagRepository briefingTagRepository;
	private final QuizRepository quizRepository;
	private final TopicModelWordRepository topicModelWordRepository;

	private final BriefingMapper briefingMapper;

	public BriefingService(BriefingRepository briefingRepository,
		BriefingTagRepository briefingTagRepository,
		QuizRepository quizRepository,
		TopicModelWordRepository topicModelWordRepository,
		BriefingMapper briefingMapper) {

		this.briefingRepository = briefingRepository;
		this.briefingTagRepository = briefingTagRepository;
		this.quizRepository = quizRepository;
		this.topicModelWordRepository = topicModelWordRepository;
		this.briefingMapper = briefingMapper;
	}

	public BriefHistoryResponseDto getMyBriefings(Long userId, int page) {
		Page<Briefing> briefingPage = briefingRepository.findByUserId(userId, PageRequest.of(page - 1, 5));
		List<Briefing> briefings = briefingPage.getContent();
		List<BriefingTag> briefCategories = briefingTagRepository.findByBrief(briefings);
		List<Quiz> quizzes = quizRepository.findByBriefingIn(briefings);

		Map<Long, List<BriefingTag>> briefCategoryByBrief = briefCategories.stream()
			.collect(Collectors.groupingBy(v -> v.getBriefing().getId()));
		Map<Long, List<Quiz>> quizzesByBrief = quizzes.stream()
			.collect(Collectors.groupingBy(v -> v.getBriefing().getId()));

		return briefingMapper.toBriefHistoryResponseDto(briefings, briefCategoryByBrief, quizzesByBrief, briefingPage);
	}

	public BriefingDetailResponseDto getBrief(Long userId, Long briefId) {
		Briefing briefing = briefingRepository.findByUserIdAndId(userId, briefId)
			.orElseThrow(
				() -> new NotFoundResourceException(format("존재하지 않는 사용자입니다. userId -> {0}", userId), POLICY_VIOLATION));

		boolean canAccess = briefing.getUserId().equals(userId);
		if (!canAccess) {
			throw new BusinessException("잘못된 접근입니다.", ErrorProtocol.BUSINESS_VIOLATION);
		}

		List<BriefingTag> briefCategories = briefingTagRepository.findByBriefing(briefing);
		List<Quiz> quizzes = quizRepository.findByBriefing(briefing);
		List<TopicModelWord> topicModelWords = topicModelWordRepository.findByBriefing(briefing);

		return briefingMapper.toBriefingDetailResponseDto(briefing, briefCategories, quizzes, topicModelWords);
	}

}
