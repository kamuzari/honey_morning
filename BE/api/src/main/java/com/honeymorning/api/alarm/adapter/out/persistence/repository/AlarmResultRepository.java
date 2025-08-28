package com.honeymorning.api.alarm.adapter.out.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honeymorning.api.alarm.adapter.out.persistence.entity.AlarmResultEntity;

public interface AlarmResultRepository extends JpaRepository<AlarmResultEntity, Long> {
	@Query("SELECT a FROM AlarmResultEntity a WHERE a.userId = :userId and a.id < :lastId ORDER BY a.createdAt DESC limit 10")
	List<AlarmResultEntity> findNextPage(@Param("userId") Long userId, @Param("lastId") Long lastId);
}
