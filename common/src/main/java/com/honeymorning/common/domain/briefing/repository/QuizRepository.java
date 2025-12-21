package com.honeymorning.common.domain.briefing.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honeymorning.common.domain.briefing.entity.BriefingEntity;
import com.honeymorning.common.domain.briefing.entity.QuizEntity;

public interface QuizRepository extends JpaRepository<QuizEntity, Long> {
	List<QuizEntity> findByBriefingEntityIn(List<BriefingEntity> briefingEntity);

	List<QuizEntity> findByBriefingEntity(BriefingEntity briefingEntity);

	Optional<QuizEntity> findByBriefingEntityIdAndSequenceOrder(Long briefingId, Integer sequenceOrder);
}
