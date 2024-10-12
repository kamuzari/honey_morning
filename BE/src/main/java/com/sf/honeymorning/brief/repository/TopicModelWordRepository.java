package com.sf.honeymorning.brief.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sf.honeymorning.brief.entity.TopicModel;
import com.sf.honeymorning.domain.brief.entity.TopicModelWord;

public interface TopicModelWordRepository extends JpaRepository<TopicModelWord, Long> {
	List<TopicModelWord> findByTopicModel(TopicModel topicModel);
}
