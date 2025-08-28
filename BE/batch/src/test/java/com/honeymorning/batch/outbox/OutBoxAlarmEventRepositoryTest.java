package com.honeymorning.batch.outbox;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.honeymorning.batch.mock.MockPersistenceTest;

class OutBoxAlarmEventRepositoryTest extends MockPersistenceTest {

	@Autowired
	OutBoxAlarmEventRepository outBoxAlarmEventRepository;

	@BeforeEach
	void updateUp() {
		outBoxAlarmEventRepository.save(OutBoxAlarmEvent.initialize(
			1L,
			"payload"
		));
	}

	@Test
	@DisplayName("아웃박스 테스트를 위한 상위 데이터 가져오기")
	void testGetTopData() {
		//given
		//when
		OutBoxAlarmEvent outBoxAlarmEvent = outBoxAlarmEventRepository.findTopByEventStatus(EventStatus.PENDING)
			.orElseThrow(RuntimeException::new);

		//then
		assertThat(outBoxAlarmEvent).isNotNull();
		assertThat(outBoxAlarmEvent.getCreatedAt()).isNotNull();
		assertThat(outBoxAlarmEvent.getEventStatus()).isEqualTo(EventStatus.PENDING);
		assertThat(outBoxAlarmEvent.getProcessedAt()).isNull();

	}
}