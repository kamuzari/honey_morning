package com.honeymorning.common.domain.briefing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honeymorning.common.domain.briefing.entity.BriefingEntity;
import com.honeymorning.common.domain.briefing.entity.TopicModelWordEntity;

public interface TopicModelWordRepository extends JpaRepository<TopicModelWordEntity, Long> {
	List<TopicModelWordEntity> findByBriefingEntity(BriefingEntity briefingEntity);
}
