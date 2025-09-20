package com.honeymorning.relay;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.honeymorning.common.domain.briefing.entity.BriefingEntity;
import com.honeymorning.common.domain.briefing.entity.BriefingTagEntity;
import com.honeymorning.common.domain.briefing.repository.BriefingRepository;
import com.honeymorning.common.exception.NotFoundResourceException;
import com.honeymorning.relay.briefing.adapter.out.persistence.BriefingPersistenceAdapter;
import com.honeymorning.relay.briefing.adapter.out.persistence.mapper.BriefingPersistenceMapper;
import com.honeymorning.relay.briefing.application.port.out.LoadBriefingPort;
import com.honeymorning.relay.context.infra.mock.MockTest;

class LoadBriefingPortTest extends MockTest {

	LoadBriefingPort sut;

	@InjectMocks
	BriefingPersistenceAdapter sutImpl;

	@Mock
	BriefingRepository briefingRepository;

	@Spy
	BriefingPersistenceMapper briefingPersistenceMapper;

	@BeforeEach
	void setUp() {
		this.sut = sutImpl;
	}

	@DisplayName("브리핑 데이터가 존재하지 않으면 비즈니스예외가 발생한다")
	@Test
	void failNotExistBriefing() {
		//given
		//when
		//then
		assertThatThrownBy(() -> sut.getTtsBriefingWithQuizzes(anyLong()))
			.isInstanceOf(NotFoundResourceException.class);
	}

	@DisplayName("퀴즈 데이터가 존재하지 않으면 비즈니스예외가 발생한다")
	@Test
	void failNotExistQuizzes() {
		// given
		BriefingEntity savedBriefingEntity = new BriefingEntity(
			MockTest.USER_ID,
			BriefingMockGenerator.GENERATOR.lorem().sentence(10),
			BriefingMockGenerator.GENERATOR.lorem().sentence(40),
			BriefingMockGenerator.GENERATOR.internet().domainName(),
			Set.of(new BriefingTagEntity(BriefingMockGenerator.GENERATOR.lorem().word())),
			Set.of(),
			BriefingMockGenerator.createTopicModelWords()
		);

		given(briefingRepository.findByIdWithQuizzes(anyLong())).willReturn(Optional.of(savedBriefingEntity));
		// when
		// then
		assertThatThrownBy(
			() -> sut.getTtsBriefingWithQuizzes(anyLong())
		).isInstanceOf(IllegalArgumentException.class);
	}
}