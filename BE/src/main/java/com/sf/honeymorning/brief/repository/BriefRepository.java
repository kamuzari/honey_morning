package com.sf.honeymorning.domain.brief.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sf.honeymorning.domain.brief.entity.Brief;
import com.sf.honeymorning.domain.user.entity.User;

public interface BriefRepository extends JpaRepository<Brief, Long> {
	Page<Brief> findByUser(User user, Pageable pageable);

	Optional<Brief> findByUserAndId(User user, Long id);

	@Query("SELECT b FROM Brief b WHERE b.user = :user AND b.createdAt >= :startOfDay AND b.createdAt < :endOfDay")
	Optional<Brief> findByUserAndCreatedAtToday(@Param("user") User user, @Param("startOfDay") LocalDateTime startOfDay,
		@Param("endOfDay") LocalDateTime endOfDay);
}
