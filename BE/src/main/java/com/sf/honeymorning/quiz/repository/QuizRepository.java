package com.sf.honeymorning.quiz.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sf.honeymorning.brief.entity.Brief;
import com.sf.honeymorning.quiz.entity.Quiz;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
	List<Quiz> findByBriefIn(List<Brief> briefs);

	Optional<List<Quiz>> findByBrief(Brief brief);
}
