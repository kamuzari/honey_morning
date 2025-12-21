package com.honeymorning.common.domain.alarm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honeymorning.common.domain.alarm.entity.TagEntity;

public interface TagRepository extends JpaRepository<TagEntity, Long> {
	Optional<TagEntity> findByWord(String word);
}
