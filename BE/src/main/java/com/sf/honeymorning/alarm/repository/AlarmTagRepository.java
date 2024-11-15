package com.sf.honeymorning.alarm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sf.honeymorning.alarm.entity.Alarm;
import com.sf.honeymorning.alarm.entity.AlarmTag;
import com.sf.honeymorning.tag.entity.Tag;

public interface AlarmTagRepository extends JpaRepository<AlarmTag, Long> {
	List<AlarmTag> findByAlarm(Alarm alarm);

	@Query("select at from AlarmTag at join fetch at.alarm a join fetch at.tag t where a = :alarm")
	List<AlarmTag> findByAlarmWithTag(@Param("alarm") Alarm alarm);

	void deleteByAlarmAndTag(Alarm alarm, Tag tag);

	AlarmTag findByTag(Tag tag);
}
