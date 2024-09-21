package com.sf.honeymorning.domain.brief.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sf.honeymorning.domain.brief.entity.Brief;
import com.sf.honeymorning.domain.brief.entity.WordCloud;

public interface WordCloudRepository extends JpaRepository<WordCloud, Long> {
	List<WordCloud> findByBrief(Brief brief);
}
