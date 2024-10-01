package com.sf.honeymorning.domain.alarm.repository;

import com.sf.honeymorning.domain.alarm.entity.Alarm;
import com.sf.honeymorning.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sf.honeymorning.domain.alarm.entity.Alarm;
import com.sf.honeymorning.domain.user.entity.User;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
	Optional<Alarm> findByUser(User user);

	@Query("SELECT a FROM Alarm a WHERE a.alarmTime BETWEEN :start AND :end AND a.isActive = :isActive")
	List<Alarm> findAlarmsWithinTimeRange(@Param("start") LocalTime start, @Param("end") LocalTime end,
		@Param("isActive") Integer isActive);
}
