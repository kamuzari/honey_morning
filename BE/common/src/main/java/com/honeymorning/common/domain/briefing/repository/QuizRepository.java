package com.honeymorning.common.domain.briefing.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honeymorning.common.domain.briefing.entity.BriefingEntity;
import com.honeymorning.common.domain.briefing.entity.QuizEntity;

public interface QuizRepository extends JpaRepository<QuizEntity, Long> {
	List<QuizEntity> findByBriefingEntityIn(List<BriefingEntity> briefingEntity);

	List<QuizEntity> findByBriefingEntity(BriefingEntity briefingEntity);

	List<QuizEntity> findByIdInAndBriefingEntityId(Collection<Long> ids, Long briefingId);

}
