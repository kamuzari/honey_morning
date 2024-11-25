package com.sf.honeymorning.alarm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sf.honeymorning.alarm.entity.AlarmResult;

public interface AlarmResultRepository extends JpaRepository<AlarmResult, Long> {
	List<AlarmResult> findByUserId(Long userId);

	List<AlarmResult> findByUserIdOrderByCreatedAt(Long userId);
}
