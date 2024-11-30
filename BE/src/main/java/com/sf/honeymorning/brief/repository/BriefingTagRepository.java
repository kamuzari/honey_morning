package com.sf.honeymorning.brief.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.entity.BriefingTag;

public interface BriefingTagRepository extends JpaRepository<BriefingTag, Long> {
	@Query("SELECT distinct bc FROM BriefingTag bc JOIN FETCH bc.tag WHERE bc.briefing IN :briefings")
	List<BriefingTag> findByBrief(@Param("briefings") List<Briefing> briefings);

	List<BriefingTag> findByBriefing(Briefing briefs);
}
