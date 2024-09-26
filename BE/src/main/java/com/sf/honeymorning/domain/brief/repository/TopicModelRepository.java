package com.sf.honeymorning.domain.brief.repository;

import com.sf.honeymorning.domain.brief.entity.Brief;
import com.sf.honeymorning.domain.brief.entity.TopicModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicModelRepository extends JpaRepository<TopicModel, Long> {
    List<TopicModel> findByBrief(Brief brief);
}
