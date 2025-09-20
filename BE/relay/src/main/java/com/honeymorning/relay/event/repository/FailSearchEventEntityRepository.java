package com.honeymorning.relay.event.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honeymorning.relay.event.entity.FailSearchEventEntity;

public interface FailSearchEventEntityRepository extends JpaRepository<FailSearchEventEntity, Long> {

	@Query(value = """
        SELECT * FROM fail_search_events
		WHERE event_status = 'FAILED'
        ORDER BY id
        LIMIT :limit
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
	Optional<FailSearchEventEntity> findFailStatusForUpdateSkipLocked(@Param("limit") Long limit);

	Optional<FailSearchEventEntity> findByBriefingId(Long id);
}