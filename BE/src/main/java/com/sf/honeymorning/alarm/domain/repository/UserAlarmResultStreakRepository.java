package com.sf.honeymorning.alarm.domain.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.sf.honeymorning.alarm.domain.entity.UserAlarmResultStreak;

public interface UserAlarmResultStreakRepository extends CrudRepository<UserAlarmResultStreak, Long> {
	Optional<UserAlarmResultStreak> findByUserId(Long userId);
}
