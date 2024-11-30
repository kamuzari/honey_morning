package com.sf.honeymorning.quiz.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.quiz.entity.Quiz;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
	List<Quiz> findByBriefingIn(List<Briefing> briefing);

	List<Quiz> findByBriefing(Briefing briefing);
}
