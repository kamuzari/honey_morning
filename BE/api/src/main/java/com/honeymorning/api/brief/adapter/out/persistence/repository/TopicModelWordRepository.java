package com.honeymorning.api.brief.adapter.out.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honeymorning.api.brief.adapter.out.persistence.entity.BriefingEntity;
import com.honeymorning.api.brief.adapter.out.persistence.entity.TopicModelWordEntity;

public interface TopicModelWordRepository extends JpaRepository<TopicModelWordEntity, Long> {
	List<TopicModelWordEntity> findByBriefingEntity(BriefingEntity briefingEntity);
}
