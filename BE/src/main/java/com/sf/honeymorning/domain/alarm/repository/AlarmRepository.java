package com.sf.honeymorning.domain.alarm.repository;

import com.sf.honeymorning.domain.alarm.entity.Alarm;
import com.sf.honeymorning.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Optional<Alarm> findByUser(User user);


    List<Alarm> findByAlarmTime(@Param("alarmTime") LocalTime alarmTime);
}
