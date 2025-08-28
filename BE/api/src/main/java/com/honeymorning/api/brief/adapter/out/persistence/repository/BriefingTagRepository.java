package com.honeymorning.api.brief.adapter.out.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honeymorning.api.brief.adapter.out.persistence.entity.BriefingEntity;
import com.honeymorning.api.brief.adapter.out.persistence.entity.BriefingTagEntity;

public interface BriefingTagRepository extends JpaRepository<BriefingTagEntity, Long> {
	@Query("SELECT distinct bt FROM BriefingTagEntity bt WHERE bt.briefingEntity IN :briefings")
	List<BriefingTagEntity> findByBrief(@Param("briefings") List<BriefingEntity> briefingEntities);

	List<BriefingTagEntity> findByBriefingEntity(BriefingEntity briefs);
}
