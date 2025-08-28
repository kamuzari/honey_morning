package com.honeymorning.api.brief.adapter.out.persistence.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honeymorning.api.brief.adapter.out.persistence.entity.BriefingEntity;

public interface BriefingRepository extends JpaRepository<BriefingEntity, Long> {
	Page<BriefingEntity> findByUserId(Long userId, Pageable pageable);

	Optional<BriefingEntity> findByUserIdAndId(Long userId, Long id);

	Optional<BriefingEntity> findTopByUserIdOrderByCreatedAtDesc(Long userId);

	@Query("SELECT b FROM BriefingEntity b join fetch b.quizEntities WHERE b.id= :briefingId")
	Optional<BriefingEntity> findByIdWithQuizzes(@Param("briefingId") Long briefingId);

	@Query("SELECT b FROM BriefingEntity b "
		+ "join fetch b.quizEntities q "
		+ "join fetch b.briefingTagEntities t "
		+ "WHERE b.id= :briefingId")
	Optional<BriefingEntity> findByIdWithQuizzesAndTopicModel(@Param("briefingId") Long briefingId);
}
