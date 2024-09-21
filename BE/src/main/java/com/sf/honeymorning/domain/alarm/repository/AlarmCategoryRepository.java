package com.sf.honeymorning.domain.alarm.repository;

import com.sf.honeymorning.domain.alarm.entity.AlarmCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlarmCategoryRepository extends JpaRepository<AlarmCategory, Long> {
    @Query("SELECT ac FROM AlarmCategory ac WHERE ac.alarm.id = :alarmId")
    List<AlarmCategory> findAllByAlarmId(@Param("alarmId") Long alarmId);

    @Modifying
    @Query("DELETE FROM AlarmCategory ac WHERE ac.alarm.id = :alarmId AND ac.tag.id = :tagId")
    void deleteByAlarmIdAndTagIds(@Param("alarmId") Long alarmId, @Param("tagId") Long tagId);

    @Query("SELECT ac FROM AlarmCategory ac WHERE ac.tag.id = :tagId")
    AlarmCategory findByTagId(@Param("tagId") Long tagId);
}
