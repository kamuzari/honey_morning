package com.sf.honeymorning.brief.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sf.honeymorning.brief.entity.Brief;

public interface BriefRepository extends JpaRepository<Brief, Long> {
	Page<Brief> findByUserId(Long userId, Pageable pageable);

	Optional<Brief> findByUserIdAndId(Long userId, Long id);

	@Query("SELECT b FROM Brief b WHERE b.userId= :user AND b.createdAt >= :startOfDay AND b.createdAt < :endOfDay")
	Optional<Brief> findByUserAndCreatedAtToday(@Param("user") Long userId,
		@Param("startOfDay") LocalDateTime startOfDay,
		@Param("endOfDay") LocalDateTime endOfDay);
}
