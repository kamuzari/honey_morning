package com.sf.honeymorning.domain.brief.repository;

import com.sf.honeymorning.domain.brief.entity.TopicModel;
import com.sf.honeymorning.domain.brief.entity.TopicModelWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicModelWordRepository extends JpaRepository<TopicModelWord, Long> {
    List<TopicModelWord> findByTopicModel(TopicModel topicModel);
}
