package com.sf.honeymorning.alarm.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sf.honeymorning.alarm.domain.entity.AlarmResult;

public interface AlarmResultRepository extends JpaRepository<AlarmResult, Long> {
	List<AlarmResult> findByUserId(Long userId);

	List<AlarmResult> findByUserIdOrderByCreatedAt(Long userId);

	@Query("SELECT a FROM AlarmResult a WHERE a.userId = :userId and a.id < :lastId ORDER BY a.createdAt DESC limit 10")
	List<AlarmResult> findNextPage(@Param("userId") Long userId, @Param("lastId") Long lastId);
}
