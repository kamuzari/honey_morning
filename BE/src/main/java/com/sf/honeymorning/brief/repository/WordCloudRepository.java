package com.sf.honeymorning.brief.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.entity.WordCloud;

public interface WordCloudRepository extends JpaRepository<WordCloud, Long> {
	List<WordCloud> findByBriefing(Briefing briefing);
}
