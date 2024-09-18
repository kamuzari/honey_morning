package com.sf.honeymorning.domain.alarm.repository;

import com.sf.honeymorning.domain.alarm.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    @Query("SELECT a FROM Alarm a WHERE a.user.id = :userId")
    Alarm findAlarmsByUserId(@Param("userId") Long userId);

    @Query("DELETE FROM Alarm a WHERE a.user.id = :userId")
    Alarm deleteAlarmsByUserId(@Param("userId") Long userId);
}
