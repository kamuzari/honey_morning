package com.sf.honeymorning.brief.adapter.out.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.QuizEntity;

public interface QuizRepository extends JpaRepository<QuizEntity, Long> {
	List<QuizEntity> findByBriefingEntityIn(List<BriefingEntity> briefingEntity);

	List<QuizEntity> findByBriefingEntity(BriefingEntity briefingEntity);
}
