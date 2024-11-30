package com.sf.honeymorning.brief.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.entity.TopicModel;

public interface TopicModelRepository extends JpaRepository<TopicModel, Long> {
	List<TopicModel> findByBriefing(Briefing briefing);
}
