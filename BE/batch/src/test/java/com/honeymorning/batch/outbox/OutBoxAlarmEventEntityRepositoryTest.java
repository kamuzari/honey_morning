package com.honeymorning.batch.outbox;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.honeymorning.batch.mock.MockPersistenceTest;
import com.honeymorning.common.domain.event.entity.EventStatus;
import com.honeymorning.common.domain.event.entity.OutBoxAlarmEventEntity;
import com.honeymorning.common.domain.event.repository.OutBoxAlarmEventRepository;

class OutBoxAlarmEventEntityRepositoryTest extends MockPersistenceTest {

	@Autowired
	OutBoxAlarmEventRepository outBoxAlarmEventRepository;

	@BeforeEach
	void updateUp() {
		outBoxAlarmEventRepository.save(OutBoxAlarmEventEntity.initialize(
			1L,
			"payload"
		));
	}

	@Test
	@DisplayName("아웃박스 테스트를 위한 상위 데이터 가져오기")
	void testGetTopData() {
		//given
		//when
		OutBoxAlarmEventEntity outBoxAlarmEventEntity = outBoxAlarmEventRepository.findTopByEventStatus(
				EventStatus.PENDING)
			.orElseThrow(RuntimeException::new);

		//then
		assertThat(outBoxAlarmEventEntity).isNotNull();
		assertThat(outBoxAlarmEventEntity.getCreatedAt()).isNotNull();
		assertThat(outBoxAlarmEventEntity.getEventStatus()).isEqualTo(EventStatus.PENDING);
		assertThat(outBoxAlarmEventEntity.getProcessedAt()).isNull();

	}
}