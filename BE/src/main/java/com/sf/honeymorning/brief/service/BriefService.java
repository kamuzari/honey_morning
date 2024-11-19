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

import com.sf.honeymorning.brief.entity.Brief;
import com.sf.honeymorning.brief.entity.BriefCategory;
import com.sf.honeymorning.brief.repository.BriefCategoryRepository;
import com.sf.honeymorning.brief.repository.BriefRepository;
import com.sf.honeymorning.brief.repository.WordCloudRepository;
import com.sf.honeymorning.domain.brief.dto.response.BriefDetailResponseDto;
import com.sf.honeymorning.domain.brief.dto.response.BriefHistoryResponseDto;
import com.sf.honeymorning.domain.brief.dto.response.briefs.BriefHistoryDto;
import com.sf.honeymorning.domain.brief.dto.response.detail.BriefResponseDto;
import com.sf.honeymorning.domain.brief.dto.response.detail.QuizResponseDto;
import com.sf.honeymorning.domain.brief.dto.response.detail.SummaryResponseDto;
import com.sf.honeymorning.quiz.entity.Quiz;
import com.sf.honeymorning.quiz.repository.QuizRepository;
import com.sf.honeymorning.tag.entity.Tag;
import com.sf.honeymorning.user.entity.User;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BriefService {

	private final BriefRepository briefRepository;
	private final BriefCategoryRepository briefCategoryRepository;
	private final WordCloudRepository wordCloudRepository;
	private final QuizRepository quizRepository;
	private final com.sf.honeymorning.domain.brief.service.TopicModelService topicModelService;
	@Value("${file.directory.path.summary}")
	private String summaryPath;
	@Value("${file.directory.path.content}")
	private String contentPath;

	public BriefHistoryResponseDto getBriefs(Long userId, int page) {
		Page<Brief> briefPage = briefRepository.findByUserId(userId, PageRequest.of(page - 1, 5));
		List<Brief> briefs = briefPage.getContent();
		List<BriefCategory> briefCategories = briefCategoryRepository.findByBrief(briefs);
		List<Quiz> quizzes = quizRepository.findByBriefIn(briefs);
		Map<Long, List<BriefCategory>> briefCategoryByBrief = briefCategories.stream()
			.collect(Collectors.groupingBy(v -> v.getBrief().getId()));
		Map<Long, List<Quiz>> quizzesByBrief = quizzes.stream()
			.collect(Collectors.groupingBy(v -> v.getBrief().getId()));
		return new BriefHistoryResponseDto(briefs.stream()
			.map(brief -> new BriefHistoryDto(brief.getId(), brief.getCreatedAt(),
				briefCategoryByBrief.get(brief.getId()).stream().map(BriefCategory::getTag)
					.map(Tag::getWord).toList(), brief.getSummary(),
				quizzesByBrief.get(brief.getId()).stream()
					.filter(quiz -> quiz.getAnswer().equals(quiz.getSelection()))
					.count())).toList(), briefPage.getTotalPages());
	}

	public BriefDetailResponseDto getBrief(Long userId, Long briefId) {
		Brief brief = briefRepository.findByUserIdAndId(userId, briefId)
			.orElseThrow(() -> new EntityNotFoundException("not exist user"));
		boolean canAccess = brief.getUserId().equals(userId);
		if (!canAccess) {
			throw new IllegalArgumentException("invalid brief");
		}
		List<BriefCategory> briefCategories = briefCategoryRepository.findByBrief(brief);
		List<Quiz> quizzes = quizRepository.findByBrief(brief)
			.orElseThrow(() -> new EntityNotFoundException("not exist user"));
		return new BriefDetailResponseDto(briefId,
			new SummaryResponseDto(topicModelService.getTopicModel(briefId),
				briefCategories.stream()
					.map(briefCategory -> briefCategory.getTag().getWord()).toList()),
			new BriefResponseDto(brief.getSummary(), brief.getContent(),
				brief.getVoiceContentUrl()), quizzes.stream()
			.map(quiz -> new QuizResponseDto(quiz.getQuestion(), quiz.getOption1(),
				quiz.getOption2(), quiz.getOption3(), quiz.getOption4(),
				quiz.getSelection(), quiz.getAnswer())).toList(), brief.getCreatedAt());
	}

	public Resource getBriefSummaryAudio(Long briefId) throws IOException {
		Brief brief = briefRepository.findById(briefId)
			.orElseThrow(
				() -> new EntityNotFoundException("Brief not found with alarmId: " + briefId));

		Path filePath = Paths.get(summaryPath, brief.getVoiceContentUrl());
		log.info("파일을 찾습니다: " + filePath);
		Resource resource = new UrlResource(filePath.toUri());

		if (resource.exists() || resource.isReadable()) {
			log.info("파일을 찾았습니다: " + resource.getFilename());
			return resource;
		} else {
			throw new IOException("Could not read the file: " + brief.getVoiceContentUrl());
		}
	}

	public Resource getBrieContentAudio(Long briefId) throws IOException {
		Brief brief = briefRepository.findById(briefId)
			.orElseThrow(
				() -> new EntityNotFoundException("Brief not found with alarmId: " + briefId));

		Path filePath = Paths.get(contentPath, brief.getVoiceContentUrl());
		log.info("파일을 찾습니다: " + filePath);
		Resource resource = new UrlResource(filePath.toUri());

		if (resource.exists() || resource.isReadable()) {
			log.info("파일을 찾았습니다: " + resource.getFilename());
			return resource;
		} else {
			throw new IOException("Could not read the file: " + brief.getVoiceContentUrl());
		}
	}

}
