package com.sf.honeymorning.domain.alarm.repository;

import com.sf.honeymorning.domain.alarm.entity.Alarm;
import com.sf.honeymorning.domain.alarm.entity.AlarmCategory;
import com.sf.honeymorning.domain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmCategoryRepository extends JpaRepository<AlarmCategory, Long> {
    List<AlarmCategory> findByAlarm(Alarm alarm);

    void deleteByAlarmAndTag(Alarm alarm, Tag tag);

    AlarmCategory findByTag(Tag tag);
}
