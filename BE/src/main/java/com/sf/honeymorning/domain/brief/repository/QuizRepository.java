package com.sf.honeymorning.domain.brief.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sf.honeymorning.domain.brief.entity.Brief;
import com.sf.honeymorning.domain.quiz.entity.Quiz;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
	List<Quiz> findByBriefIn(List<Brief> briefs);
}
