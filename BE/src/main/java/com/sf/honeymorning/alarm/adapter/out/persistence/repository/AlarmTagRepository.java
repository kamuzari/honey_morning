package com.sf.honeymorning.alarm.adapter.out.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sf.honeymorning.alarm.adapter.out.persistence.entity.AlarmEntity;
import com.sf.honeymorning.alarm.adapter.out.persistence.entity.TagEntity;
import com.sf.honeymorning.alarm.adapter.out.persistence.entity.AlarmTagEntity;

public interface AlarmTagRepository extends JpaRepository<AlarmTagEntity, Long> {
	boolean existsByAlarmEntityAndTagEntity(AlarmEntity alarmEntity, TagEntity tagEntity);

	@Query("select at from AlarmTagEntity at join fetch at.alarmEntity a join fetch at.tagEntity t where a = :alarm")
	List<AlarmTagEntity> findByAlarmWithTag(@Param("alarm") AlarmEntity alarmEntity);

	void deleteByAlarmEntityAndTagEntity(AlarmEntity alarmEntity, TagEntity tagEntity);
}
