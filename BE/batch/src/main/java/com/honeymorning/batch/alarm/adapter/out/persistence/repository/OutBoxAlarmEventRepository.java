package com.honeymorning.batch.alarm.adapter.out.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honeymorning.batch.alarm.adapter.out.persistence.entity.EventStatus;
import com.honeymorning.batch.alarm.adapter.out.persistence.entity.OutBoxAlarmEventEntity;

public interface OutBoxAlarmEventRepository extends JpaRepository<OutBoxAlarmEventEntity, Long> {
	Optional<OutBoxAlarmEventEntity> findTopByEventStatus(EventStatus eventStatus);
}
