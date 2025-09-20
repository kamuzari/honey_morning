package com.honeymorning.relay.event.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honeymorning.relay.event.entity.FailTtsEventEntity;

public interface FailTtsEventEntityRepository extends JpaRepository<FailTtsEventEntity, Long> {
	@Query(value = """
        SELECT * FROM fail_tts_events
		WHERE event_status = 'FAILED'
        ORDER BY id
        LIMIT :limit
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
	Optional<FailTtsEventEntity> findFailStatusForUpdateSkipLocked(@Param("limit") Long limit);

	Optional<FailTtsEventEntity> findByBriefingId(Long briefingId);
}