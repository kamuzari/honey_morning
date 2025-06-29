package com.sf.honeymorning.brief.application.port.out;

import static com.sf.honeymorning.brief.utils.BriefingMockGenerator.GENERATOR;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingTagEntity;
import com.sf.honeymorning.brief.utils.BriefingMockGenerator;
import com.sf.honeymorning.brief.adapter.out.persistence.BriefingPersistenceAdapter;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.mapper.BriefingPersistenceMapper;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.BriefingRepository;
import com.sf.honeymorning.common.exception.model.NotFoundResourceException;
import com.sf.honeymorning.context.mock.MockTest;

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
		BriefingEntity savedBriefingEntity =new BriefingEntity(
			AUTH_USER_ENTITY.getId(),
			GENERATOR.lorem().sentence(10),
			GENERATOR.lorem().sentence(40),
			GENERATOR.internet().domainName(),
			Set.of(new BriefingTagEntity(GENERATOR.lorem().word())),
			Set.of(),
			BriefingMockGenerator.createTopicModelWords()
		);

		given(briefingRepository.findByIdWithQuizzes(anyLong())).willReturn(Optional.of(savedBriefingEntity));
		// when
		// then
		Assertions.assertThatThrownBy(
			() -> sut.getTtsBriefingWithQuizzes(anyLong())
		).isInstanceOf(IllegalArgumentException.class);
	}
}