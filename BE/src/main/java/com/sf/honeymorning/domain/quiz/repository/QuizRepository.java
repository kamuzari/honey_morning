package com.sf.honeymorning.domain.quiz.repository;

import com.sf.honeymorning.domain.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    @Query("SELECT q FROM Quiz q WHERE q.")
    Optional<Quiz> findByUserId(Long userId);
}
