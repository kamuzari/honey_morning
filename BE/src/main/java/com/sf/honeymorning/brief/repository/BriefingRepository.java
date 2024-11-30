package com.sf.honeymorning.brief.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sf.honeymorning.brief.entity.Briefing;

public interface BriefingRepository extends JpaRepository<Briefing, Long> {
	Page<Briefing> findByUserId(Long userId, Pageable pageable);

	Optional<Briefing> findByUserIdAndId(Long userId, Long id);

	@Query("SELECT b FROM Briefing b WHERE b.userId= :user AND b.createdAt >= :startOfDay AND b.createdAt < :endOfDay")
	Optional<Briefing> findByUserAndCreatedAtToday(@Param("user") Long userId,
		@Param("startOfDay") LocalDateTime startOfDay,
		@Param("endOfDay") LocalDateTime endOfDay);
}
