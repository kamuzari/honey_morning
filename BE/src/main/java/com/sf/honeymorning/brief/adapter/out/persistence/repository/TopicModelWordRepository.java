package com.sf.honeymorning.brief.adapter.out.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.TopicModelWordEntity;

public interface TopicModelWordRepository extends JpaRepository<TopicModelWordEntity, Long> {
	List<TopicModelWordEntity> findByBriefingEntity(BriefingEntity briefingEntity);
}
