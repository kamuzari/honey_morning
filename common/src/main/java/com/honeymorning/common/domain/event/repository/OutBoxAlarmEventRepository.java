package com.honeymorning.common.domain.event.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honeymorning.common.domain.event.entity.EventStatus;
import com.honeymorning.common.domain.event.entity.OutBoxAlarmEventEntity;

public interface OutBoxAlarmEventRepository extends JpaRepository<OutBoxAlarmEventEntity, Long> {
	Optional<OutBoxAlarmEventEntity> findTopByEventStatus(EventStatus eventStatus);
}
