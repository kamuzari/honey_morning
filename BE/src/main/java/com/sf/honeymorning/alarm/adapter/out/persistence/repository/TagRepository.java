package com.sf.honeymorning.alarm.adapter.out.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sf.honeymorning.alarm.adapter.out.persistence.entity.TagEntity;

public interface TagRepository extends JpaRepository<TagEntity, Long> {
	Optional<TagEntity> findByWord(String word);
}
