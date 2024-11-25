package com.sf.honeymorning.alarm.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sf.honeymorning.alarm.entity.Alarm;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
	Optional<Alarm> findByUserId(Long userId);

	@Query("SELECT a FROM Alarm a WHERE a.dayOfWeek BETWEEN :start AND :end AND a.isActive = :isActive")
	List<Alarm> findAlarmsWithinTimeRange(@Param("start") LocalTime start, @Param("end") LocalTime end,
		@Param("isActive") Integer isActive);

	@Query(value = "SELECT * FROM alarms a " +
		"WHERE a.is_active = true " +
		"AND (a.day_of_week & :dayOfWeekMask) != 0 " +
		"AND a.wake_up_time = :endTime",
		nativeQuery = true)
	List<Alarm> findActiveAlarmsForToday(
		@Param("dayOfWeekMask") byte dayOfWeekMask,
		@Param("endTime") LocalTime endTime
	);
}
