package com.sf.honeymorning.alarm.adapter.out.persistence.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.sf.honeymorning.alarm.adapter.out.persistence.entity.UserAlarmResultStreakEntity;

public interface UserAlarmResultStreakRepository extends CrudRepository<UserAlarmResultStreakEntity, Long> {
	Optional<UserAlarmResultStreakEntity> findByUserId(Long userId);
}
