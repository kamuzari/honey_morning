package com.sf.honeymorning.domain.tag.repository;

import com.sf.honeymorning.domain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByWord(String word);
}
