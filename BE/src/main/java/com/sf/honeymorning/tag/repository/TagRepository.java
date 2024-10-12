package com.sf.honeymorning.tag.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sf.honeymorning.tag.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
	Optional<Tag> findByWord(String word);
}
