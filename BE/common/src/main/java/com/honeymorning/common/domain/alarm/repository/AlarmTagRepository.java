package com.honeymorning.common.domain.alarm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honeymorning.common.domain.alarm.entity.AlarmEntity;
import com.honeymorning.common.domain.alarm.entity.AlarmTagEntity;
import com.honeymorning.common.domain.alarm.entity.TagEntity;

public interface AlarmTagRepository extends JpaRepository<AlarmTagEntity, Long> {
	boolean existsByAlarmEntityAndTagEntity(AlarmEntity alarmEntity, TagEntity tagEntity);

	@Query("select at from AlarmTagEntity at join fetch at.alarmEntity a join fetch at.tagEntity t where a = :alarm")
	List<AlarmTagEntity> findByAlarmWithTag(@Param("alarm") AlarmEntity alarmEntity);

	void deleteByAlarmEntityAndTagEntity(AlarmEntity alarmEntity, TagEntity tagEntity);
}
