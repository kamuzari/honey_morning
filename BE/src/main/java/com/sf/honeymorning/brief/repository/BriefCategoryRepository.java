package com.sf.honeymorning.brief.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sf.honeymorning.brief.entity.Brief;
import com.sf.honeymorning.brief.entity.BriefCategory;

public interface BriefCategoryRepository extends JpaRepository<BriefCategory, Long> {
	@Query("SELECT distinct bc FROM BriefCategory bc JOIN FETCH bc.tag WHERE bc.brief IN :briefs")
	List<BriefCategory> findByBrief(@Param("briefs") List<Brief> briefs);

	List<BriefCategory> findByBrief(Brief briefs);
}
