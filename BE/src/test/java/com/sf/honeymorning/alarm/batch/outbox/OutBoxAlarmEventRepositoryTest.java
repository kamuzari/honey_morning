package com.sf.honeymorning.alarm.batch.outbox;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sf.honeymorning.context.RepositoryMockTest;

class OutBoxAlarmEventRepositoryTest extends RepositoryMockTest {
	@Autowired
	OutBoxAlarmEventRepository outBoxAlarmEventRepository;

	@BeforeEach
	public void setUp() {
		outBoxAlarmEventRepository.saveAndFlush(OutBoxAlarmEvent.initialize(
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
		assertThat(outBoxAlarmEvent.getCreateAt()).isNotNull();
		assertThat(outBoxAlarmEvent.getEventStatus()).isEqualTo(EventStatus.PENDING);
		assertThat(outBoxAlarmEvent.getProcessedAt()).isNull();

	}
}