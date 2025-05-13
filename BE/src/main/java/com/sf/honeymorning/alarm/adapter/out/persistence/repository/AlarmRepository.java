package com.sf.honeymorning.alarm.adapter.out.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.sf.honeymorning.alarm.adapter.out.persistence.entity.AlarmEntity;

public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {
	Optional<AlarmEntity> findByUserId(Long userId);

	Optional<AlarmEntity> findByUserIdAndIsActiveTrue(Long userId);
}
