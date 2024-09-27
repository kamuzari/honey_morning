package com.sf.honeymorning.domain.brief.repository;

import com.sf.honeymorning.domain.brief.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WordRepository extends JpaRepository<Word, Long> {
    Boolean existsByWord(String word);

    Word findByWord(String word);
}
