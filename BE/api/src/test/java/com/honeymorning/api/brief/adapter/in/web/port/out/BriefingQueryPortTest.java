package com.honeymorning.api.brief.adapter.in.web.port.out;

import static com.honeymorning.common.domain.briefing.constraint.QuizConstraint.ANSWER_MAXIMUM_VALUE;
import static com.honeymorning.common.domain.briefing.constraint.QuizConstraint.ANSWER_MINIMUM_VALUE;
import static com.honeymorning.api.brief.utils.BriefingMockGenerator.GENERATOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;

import com.honeymorning.api.brief.adapter.in.web.dto.response.BriefingDetailResponseDto;
import com.honeymorning.api.brief.adapter.out.persistence.BriefingPersistenceAdapter;
import com.honeymorning.api.brief.adapter.out.persistence.mapper.BriefingPersistenceMapper;
import com.honeymorning.api.context.mock.MockTest;
import com.honeymorning.common.common.content.AccessAuthority;
import com.honeymorning.common.common.content.Content;
import com.honeymorning.common.common.content.FileType;
import com.honeymorning.common.domain.briefing.entity.BriefingEntity;
import com.honeymorning.common.domain.briefing.entity.QuizEntity;
import com.honeymorning.common.domain.briefing.entity.TopicModelWordEntity;
import com.honeymorning.common.domain.briefing.repository.BriefingRepository;
import com.honeymorning.common.domain.briefing.repository.BriefingTagRepository;
import com.honeymorning.common.domain.briefing.repository.QuizRepository;
import com.honeymorning.common.domain.briefing.repository.TopicModelWordRepository;
import com.honeymorning.common.exception.NotFoundResourceException;

public class BriefingQueryPortTest extends MockTest {
	BriefingQueryPort sut;

	@InjectMocks
	BriefingPersistenceAdapter briefingPersistenceAdapter;

	@Mock
	BriefingRepository briefingRepository;

	@Mock
	BriefingTagRepository briefingTagRepository;

	@Mock
	QuizRepository quizRepository;

	@Mock
	TopicModelWordRepository topicModelWordRepository;

	@Spy
	BriefingPersistenceMapper briefingMapper;

	@BeforeEach
	public void setUp() {
		sut = briefingPersistenceAdapter;
	}

	@DisplayName("나의 브리핑 상세목록을 가져온다")
	@Test
	void testGetDetailBriefing() {
		//given
		BriefingEntity briefingEntity = new BriefingEntity(
			AUTH_USER_ENTITY.getId(),
			GENERATOR.lorem().sentence(10),
			GENERATOR.lorem().word(),
			GENERATOR.internet().url());
		briefingEntity.addWakeUpBriefingContent(new Content(
			GENERATOR.internet().domainName(),
			(long)GENERATOR.number().numberBetween(1000, 100_000),
			FileType.BRIEFING,
			GENERATOR.internet().url().toLowerCase(),
			AccessAuthority.PART_ALLOWED)
		);
		ReflectionTestUtils.setField(briefingEntity, "id", 1L);
		List<QuizEntity> quizEntities = List.of(new QuizEntity(GENERATOR.lorem().sentence(),
				GENERATOR.number().numberBetween(ANSWER_MINIMUM_VALUE, ANSWER_MAXIMUM_VALUE),
				Stream.generate(() -> GENERATOR.lorem().sentence()).limit(4).toList()),
			new QuizEntity(GENERATOR.lorem().sentence(),
				GENERATOR.number().numberBetween(ANSWER_MINIMUM_VALUE, ANSWER_MAXIMUM_VALUE),
				Stream.generate(() -> GENERATOR.lorem().sentence()).limit(4).toList())
		);

		quizEntities.forEach(quiz -> ReflectionTestUtils.setField(quiz, "briefingEntity", briefingEntity));

		List<TopicModelWordEntity> topicModelWordEntities = Stream.generate(() -> new TopicModelWordEntity(
			GENERATOR.number().numberBetween(1, 5),
			GENERATOR.lorem().word(),
			GENERATOR.number().randomDouble(2, 0, 20)
		)).limit(150).toList();

		given(briefingRepository.findByUserIdAndId(AUTH_USER_ENTITY.getId(), briefingEntity.getId()))
			.willReturn(Optional.of(briefingEntity));
		given(quizRepository.findByBriefingEntity(briefingEntity)).willReturn(quizEntities);
		given(topicModelWordRepository.findByBriefingEntity(briefingEntity)).willReturn(topicModelWordEntities);

		//when
		BriefingDetailResponseDto briefDetailResponseDto = sut.getMyBriefing(AUTH_USER_ENTITY.getId(),
			briefingEntity.getId());

		//then
		assertThat(briefDetailResponseDto).isNotNull();
		assertThat(briefDetailResponseDto.briefId()).isNotNull();

		verify(briefingRepository, times(1)).findByUserIdAndId(any(), any());
		verify(briefingTagRepository, times(1)).findByBriefingEntity(any());
		verify(quizRepository, times(1)).findByBriefingEntity(any());
		verify(topicModelWordRepository, times(1)).findByBriefingEntity(any());
	}

	@DisplayName("나의 브리핑 상세목록이 아닌것에 접근할 수 없다")
	@Test
	void failGetDetailBriefing() {
		//given
		Long anotherUserId = 9L;
		BriefingEntity briefingEntity = new BriefingEntity(anotherUserId,
			GENERATOR.lorem().sentence(10),
			GENERATOR.lorem().word(),
			GENERATOR.internet().url());
		ReflectionTestUtils.setField(briefingEntity, "id", 1L);
		given(briefingRepository.findByUserIdAndId(AUTH_USER_ENTITY.getId(), briefingEntity.getId()))
			.willReturn(Optional.of(briefingEntity));

		//when
		//then
		assertThatThrownBy(() -> sut.getMyBriefing(AUTH_USER_ENTITY.getId(), briefingEntity.getId()))
			.isInstanceOf(NotFoundResourceException.class);
		verify(briefingRepository, times(1)).findByUserIdAndId(any(), any());
	}
}
