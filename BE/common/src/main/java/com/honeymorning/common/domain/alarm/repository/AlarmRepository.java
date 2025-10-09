package com.honeymorning.common.domain.alarm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honeymorning.common.domain.alarm.entity.AlarmEntity;

public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {
	Optional<AlarmEntity> findByUserId(Long userId);

	Optional<AlarmEntity> findByUserIdAndIsActiveTrue(Long userId);
}
