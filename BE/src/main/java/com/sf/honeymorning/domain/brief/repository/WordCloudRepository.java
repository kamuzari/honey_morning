package com.sf.honeymorning.domain.brief.repository;

import com.sf.honeymorning.domain.brief.entity.Brief;
import com.sf.honeymorning.domain.brief.entity.WordCloud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordCloudRepository extends JpaRepository<WordCloud, Long> {
    List<WordCloud> findByBrief(Brief brief);
}
