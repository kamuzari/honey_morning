package com.sf.honeymorning.domain.brief.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.domain.auth.service.AuthService;
import com.sf.honeymorning.domain.brief.dto.response.BriefDetailResponseDto;
import com.sf.honeymorning.domain.brief.dto.response.BriefHistory;
import com.sf.honeymorning.domain.brief.dto.response.breifdetail.BriefResponseDto;
import com.sf.honeymorning.domain.brief.dto.response.breifdetail.QuizResponseDto;
import com.sf.honeymorning.domain.brief.dto.response.breifdetail.SummaryResponseDto;
import com.sf.honeymorning.domain.brief.dto.response.breifdetail.SummaryResponseDto.WordCloudResponseDto;
import com.sf.honeymorning.domain.brief.entity.Brief;
import com.sf.honeymorning.domain.brief.entity.BriefCategory;
import com.sf.honeymorning.domain.brief.entity.WordCloud;
import com.sf.honeymorning.domain.brief.repository.BriefCategoryRepository;
import com.sf.honeymorning.domain.brief.repository.BriefRepository;
import com.sf.honeymorning.domain.brief.repository.QuizRepository;
import com.sf.honeymorning.domain.brief.repository.WordCloudRepository;
import com.sf.honeymorning.domain.quiz.entity.Quiz;
import com.sf.honeymorning.domain.tag.entity.Tag;
import com.sf.honeymorning.domain.user.entity.User;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BriefService {
	private final AuthService authService;
	private final BriefRepository briefRepository;
	private final BriefCategoryRepository briefCategoryRepository;
	private final WordCloudRepository wordCloudRepository;
	private final QuizRepository quizRepository;

	public List<BriefHistory> getBriefs(String authUsername, Pageable pageable) {
		User user = authService.getLoginUser();
		Page<Brief> briefPage = briefRepository.findByUser(user, pageable);
		List<Brief> briefs = briefPage.getContent();
		List<BriefCategory> briefCategories = briefCategoryRepository.findByBrief(briefs);
		List<Quiz> quizzes = quizRepository.findByBriefIn(briefs);

		return toBriefHistoryDto(briefs, briefCategories, quizzes);
	}

	public BriefDetailResponseDto getBrief(Long briefId) {
		User user = authService.getLoginUser();
		Brief brief = briefRepository.findByUserAndId(user, briefId)
			.orElseThrow(() -> new EntityNotFoundException("not exist user"));

		boolean canAccess = brief.getUser().getId().equals(user.getId());
		if (!canAccess) {
			throw new RuntimeException("not allowed service");
		}

		List<WordCloud> wordClouds = wordCloudRepository.findByBrief(brief);
		List<BriefCategory> briefCategories = briefCategoryRepository.findByBrief(brief);
		List<Quiz> quizzes = quizRepository.findByBrief(brief);

		return toBriefDetailResponseDto(briefId, wordClouds, briefCategories, brief, quizzes);
	}

	private BriefDetailResponseDto toBriefDetailResponseDto(Long briefId, List<WordCloud> wordClouds,
		List<BriefCategory> briefCategories, Brief brief, List<Quiz> quizzes) {
		return new BriefDetailResponseDto(
			briefId,
			new SummaryResponseDto(
				wordClouds.stream()
					.map(wordCloud -> new WordCloudResponseDto(
						wordCloud.getKeyword(),
						wordCloud.getFrequency()))
					.toList(),
				briefCategories.stream()
					.map(briefCategory -> briefCategory.getTag().getWord())
					.toList()
			),
			new BriefResponseDto(
				brief.getSummary(),
				brief.getContent()
			)
			,
			quizzes.stream().map(quiz -> new QuizResponseDto(
				quiz.getQuestion(),
				quiz.getOption1(),
				quiz.getOption2(),
				quiz.getOption3(),
				quiz.getOption4(),
				quiz.getSelection(),
				quiz.getAnswer()
			)).toList()
		);
	}

	private List<BriefHistory> toBriefHistoryDto(List<Brief> briefs, List<BriefCategory> briefCategories,
		List<Quiz> quizzes) {
		Map<Long, List<BriefCategory>> briefCategoryByBrief = briefCategories.stream()
			.collect(Collectors.groupingBy(v -> v.getBrief().getId()));
		Map<Long, List<Quiz>> quizzesByBrief = quizzes.stream()
			.collect(Collectors.groupingBy(v -> v.getBrief().getId()));

		return briefs.stream().map(brief ->
			new BriefHistory(
				brief.getId(),
				brief.getCreatedAt(),
				briefCategoryByBrief.get(brief.getId()).stream()
					.map(BriefCategory::getTag)
					.map(Tag::getWord)
					.toList(),
				brief.getSummary(),
				quizzesByBrief.get(brief.getId()).stream()
					.filter(quiz -> quiz.getAnswer().equals(quiz.getSelection()))
					.count()
			)
		).toList();
	}
}
