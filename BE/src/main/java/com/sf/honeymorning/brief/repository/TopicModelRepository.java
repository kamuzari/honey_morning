package com.sf.honeymorning.domain.brief.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sf.honeymorning.domain.brief.entity.Brief;
import com.sf.honeymorning.domain.brief.entity.TopicModel;

public interface TopicModelRepository extends JpaRepository<TopicModel, Long> {
	List<TopicModel> findByBrief(Brief brief);
}
