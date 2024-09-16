package com.sf.honeymorning.domain.alarm.repository;

import com.sf.honeymorning.domain.alarm.entity.Alarm;
import com.sf.honeymorning.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    // 특정 User에 대한 Alarm을 조회하는 메서드
    Alarm findByUser(User user);
}
