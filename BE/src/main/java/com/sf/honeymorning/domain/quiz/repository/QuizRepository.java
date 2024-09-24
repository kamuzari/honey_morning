package com.sf.honeymorning.domain.quiz.repository;

import com.sf.honeymorning.domain.brief.entity.Brief;
import com.sf.honeymorning.domain.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByBriefIn(List<Brief> briefs);

    Optional<List<Quiz>> findByBrief(Brief brief);
}
