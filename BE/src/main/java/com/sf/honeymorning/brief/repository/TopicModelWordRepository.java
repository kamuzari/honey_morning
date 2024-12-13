package com.sf.honeymorning.brief.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.entity.TopicModelWord;

public interface TopicModelWordRepository extends JpaRepository<TopicModelWord, Long> {
	List<TopicModelWord> findByBriefing(Briefing briefing);
}
