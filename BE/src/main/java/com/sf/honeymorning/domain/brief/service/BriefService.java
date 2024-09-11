package com.sf.honeymorning.domain.brief.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.domain.brief.dto.response.BriefHistory;
import com.sf.honeymorning.domain.brief.entity.Brief;
import com.sf.honeymorning.domain.brief.entity.BriefCategory;
import com.sf.honeymorning.domain.brief.repository.BriefCategoryRepository;
import com.sf.honeymorning.domain.brief.repository.BriefRepository;
import com.sf.honeymorning.domain.brief.repository.QuizRepository;
import com.sf.honeymorning.domain.common.Tag;
import com.sf.honeymorning.domain.quiz.entity.Quiz;
import com.sf.honeymorning.domain.user.entity.User;
import com.sf.honeymorning.domain.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BriefService {
	private final UserRepository userRepository;
	private final BriefRepository briefRepository;
	private final BriefCategoryRepository briefCategoryRepository;
	private final QuizRepository quizRepository;

	public List<BriefHistory> getBriefs(String authUsername, Pageable pageable) {
		User user = userRepository.findByEmail(authUsername)
			.orElseThrow(() -> new EntityNotFoundException("not exist user"));
		Page<Brief> briefPage = briefRepository.findByUser(user, pageable);
		List<Brief> briefs = briefPage.getContent();
		List<BriefCategory> briefCategories = briefCategoryRepository.findByBrief(briefs);
		List<Quiz> quizzes = quizRepository.findByBriefIn(briefs);

		return toBriefHistoryDto(briefs, briefCategories, quizzes);
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
