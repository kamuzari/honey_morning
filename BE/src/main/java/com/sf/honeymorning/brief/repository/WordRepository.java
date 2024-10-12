package com.sf.honeymorning.domain.brief.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sf.honeymorning.domain.brief.entity.Word;

public interface WordRepository extends JpaRepository<Word, Long> {
	Boolean existsByWord(String word);

	Word findByWord(String word);
}
