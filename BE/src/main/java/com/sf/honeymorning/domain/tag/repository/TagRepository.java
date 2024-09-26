package com.sf.honeymorning.domain.tag.repository;

import com.sf.honeymorning.domain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByWord(String word);
}
