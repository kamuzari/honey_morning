package com.sf.honeymorning.brief.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.brief.controller.dto.response.BriefDetailResponseDto;
import com.sf.honeymorning.brief.controller.dto.response.BriefHistoryResponseDto;
import com.sf.honeymorning.brief.controller.dto.response.briefs.MyBriefing;
import com.sf.honeymorning.brief.controller.dto.response.detail.QuizResponseDto;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.entity.BriefingTag;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.brief.repository.BriefingTagRepository;
import com.sf.honeymorning.exception.model.BusinessException;
import com.sf.honeymorning.exception.model.ErrorProtocol;
import com.sf.honeymorning.quiz.entity.Quiz;
import com.sf.honeymorning.quiz.repository.QuizRepository;
import com.sf.honeymorning.tag.entity.Tag;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(readOnly = true)
@Service
public class BriefService {

	private final BriefingRepository briefingRepository;
	private final BriefingTagRepository briefingTagRepository;
	private final QuizRepository quizRepository;
	private final TopicModelService topicModelService;
	@Value("${file.directory.path.summary}")
	private String summaryPath;
	@Value("${file.directory.path.content}")
	private String contentPath;

	public BriefService(BriefingRepository briefingRepository, BriefingTagRepository briefingTagRepository,
		QuizRepository quizRepository, TopicModelService topicModelService) {
		this.briefingRepository = briefingRepository;
		this.briefingTagRepository = briefingTagRepository;
		this.quizRepository = quizRepository;
		this.topicModelService = topicModelService;
	}

	public BriefHistoryResponseDto getMyBriefings(Long userId, int page) {
		Page<Briefing> briefPage = briefingRepository.findByUserId(userId, PageRequest.of(page - 1, 5));
		List<Briefing> briefings = briefPage.getContent();
		List<BriefingTag> briefCategories = briefingTagRepository.findByBrief(briefings);
		List<Quiz> quizzes = quizRepository.findByBriefingIn(briefings);
		Map<Long, List<BriefingTag>> briefCategoryByBrief = briefCategories.stream()
			.collect(Collectors.groupingBy(v -> v.getBriefing().getId()));
		Map<Long, List<Quiz>> quizzesByBrief = quizzes.stream()
			.collect(Collectors.groupingBy(v -> v.getBriefing().getId()));

		return new BriefHistoryResponseDto(briefings.stream()
			.map(brief -> new MyBriefing(brief.getId(), brief.getCreatedAt(),
				briefCategoryByBrief.get(brief.getId()).stream().map(BriefingTag::getTag)
					.map(Tag::getWord).toList(), brief.getSummary(),
				quizzesByBrief.get(brief.getId()).stream()
					.filter(quiz -> quiz.getAnswer().equals(quiz.getSelection()))
					.count())).toList(), briefPage.getTotalPages());
	}

	public BriefDetailResponseDto getBrief(Long userId, Long briefId) {
		Briefing briefing = briefingRepository.findByUserIdAndId(userId, briefId)
			.orElseThrow(() -> new EntityNotFoundException("not exist user"));
		boolean canAccess = briefing.getUserId().equals(userId);
		if (!canAccess) {
			throw new BusinessException("잘못된 접근입니다.", ErrorProtocol.BUSINESS_VIOLATION);
		}

		List<BriefingTag> briefCategories = briefingTagRepository.findByBriefing(briefing);
		List<Quiz> quizzes = quizRepository.findByBriefing(briefing);

		return toBriefDetailResponse(briefing, briefCategories, quizzes);
	}

	private BriefDetailResponseDto toBriefDetailResponse(
		Briefing briefing,
		List<BriefingTag> briefCategories,
		List<Quiz> quizzes) {

		return new BriefDetailResponseDto(
			briefing.getId(),
			briefing.getSummary(),
			briefing.getContent(),
			briefing.getVoiceContentUrl(),
			topicModelService.getTopicModel(briefing.getId()),
			briefCategories.stream().map(briefingTag -> briefingTag.getTag().getWord()).toList(),
			quizzes.stream()
				.map(quiz -> new QuizResponseDto(
					quiz.getQuestion(),
					quiz.getOption1(),
					quiz.getOption2(),
					quiz.getOption3(),
					quiz.getOption4(),
					quiz.getSelection(),
					quiz.getAnswer()
				)).toList(),
			briefing.getCreatedAt());
	}

	public Resource getBriefSummaryAudio(Long briefId) throws IOException {
		Briefing briefing = briefingRepository.findById(briefId)
			.orElseThrow(
				() -> new EntityNotFoundException("Brief not found with alarmId: " + briefId));

		Path filePath = Paths.get(summaryPath, briefing.getVoiceContentUrl());
		log.info("파일을 찾습니다: " + filePath);
		Resource resource = new UrlResource(filePath.toUri());

		if (resource.exists() || resource.isReadable()) {
			log.info("파일을 찾았습니다: " + resource.getFilename());
			return resource;
		} else {
			throw new IOException("Could not read the file: " + briefing.getVoiceContentUrl());
		}
	}

	public Resource getBrieContentAudio(Long briefId) throws IOException {
		Briefing briefing = briefingRepository.findById(briefId)
			.orElseThrow(
				() -> new EntityNotFoundException("Brief not found with alarmId: " + briefId));

		Path filePath = Paths.get(contentPath, briefing.getVoiceContentUrl());
		log.info("파일을 찾습니다: " + filePath);
		Resource resource = new UrlResource(filePath.toUri());

		if (resource.exists() || resource.isReadable()) {
			log.info("파일을 찾았습니다: " + resource.getFilename());
			return resource;
		} else {
			throw new IOException("Could not read the file: " + briefing.getVoiceContentUrl());
		}
	}

}
