package com.sf.honeymorning.alarm.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sf.honeymorning.alarm.domain.entity.Alarm;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
	Optional<Alarm> findByUserId(Long userId);

	Optional<Alarm> findByUserIdAndIsActiveTrue(Long userId);

}
