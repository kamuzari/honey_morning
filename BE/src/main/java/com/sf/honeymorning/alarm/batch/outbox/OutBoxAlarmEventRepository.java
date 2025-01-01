package com.sf.honeymorning.alarm.batch.outbox;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OutBoxAlarmEventRepository extends JpaRepository<OutBoxAlarmEvent, Long> {
	Optional<OutBoxAlarmEvent> findTopByEventStatus(EventStatus eventStatus);
}
