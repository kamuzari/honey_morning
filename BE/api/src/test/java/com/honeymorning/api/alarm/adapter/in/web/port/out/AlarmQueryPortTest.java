package com.honeymorning.api.alarm.adapter.in.web.port.out;

import static com.honeymorning.api.brief.utils.BriefingMockGenerator.GENERATOR;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;

import com.honeymorning.api.alarm.adapter.out.persistence.AlarmPersistenceAdapter;
import com.honeymorning.api.alarm.adapter.out.persistence.mapper.AlarmPersistenceAdapterMapper;
import com.honeymorning.api.context.mock.MockTest;
import com.honeymorning.common.common.content.AccessAuthority;
import com.honeymorning.common.common.content.Content;
import com.honeymorning.common.common.content.FileType;
import com.honeymorning.common.domain.alarm.entity.AlarmEntity;
import com.honeymorning.common.domain.alarm.repository.AlarmRepository;
import com.honeymorning.common.domain.briefing.constraint.QuizConstraint;
import com.honeymorning.common.domain.briefing.entity.BriefingEntity;
import com.honeymorning.common.domain.briefing.entity.QuizEntity;
import com.honeymorning.common.domain.briefing.repository.BriefingRepository;
import com.honeymorning.common.domain.briefing.repository.QuizRepository;

class AlarmQueryPortTest extends MockTest {
	AlarmQueryPort sut;

	@InjectMocks
	AlarmPersistenceAdapter alarmPersistenceAdapter;

	@Mock
	BriefingRepository briefingRepository;

	@Mock
	AlarmRepository alarmRepository;

	@Mock
	QuizRepository quizRepository;

	@Spy
	AlarmPersistenceAdapterMapper alarmPersistenceAdapterMapper;

	@BeforeEach
	void setUp() {
		sut = alarmPersistenceAdapter;
	}

	@Test
	@DisplayName("기상전 알람 콘텐츠들을 모두 가져온다")
	void testGetPreparedAlarmContents() {
		//given
		AlarmEntity expectedAlarmEntity = new AlarmEntity(
			AUTH_USER_ENTITY.getId(),
			LocalTime.now().withSecond(0),
			2,
			2,
			2,
			true
		);
		BriefingEntity expectedBriefingEntity = new BriefingEntity(AUTH_USER_ENTITY.getId(),
			GENERATOR.lorem().sentence(10),
			GENERATOR.lorem().sentence(20),
			GENERATOR.internet().url().toLowerCase()
		);
		expectedBriefingEntity.addWakeUpBriefingContent(new Content(
			GENERATOR.internet().domainName(),
			(long)GENERATOR.number().numberBetween(1000, 100_000),
			FileType.BRIEFING,
			GENERATOR.internet().url().toLowerCase(),
			AccessAuthority.PART_ALLOWED)
		);
		List<QuizEntity> expectedQuizzes = createFakeQuiz();

		given(alarmRepository.findByUserIdAndIsActiveTrue(AUTH_USER_ENTITY.getId())).willReturn(
			Optional.of(expectedAlarmEntity));
		given(briefingRepository.findTopByUserIdOrderByCreatedAtDesc(AUTH_USER_ENTITY.getId())).willReturn(
			Optional.of(expectedBriefingEntity));
		given(quizRepository.findByBriefingEntity(expectedBriefingEntity)).willReturn(expectedQuizzes);

		//when
		var preparedAlarmContents = sut.getPreparedAlarmContents(AUTH_USER_ENTITY.getId());

		//then
		verify(alarmRepository, times(1)).findByUserIdAndIsActiveTrue(AUTH_USER_ENTITY.getId());
		verify(briefingRepository, times(1)).findTopByUserIdOrderByCreatedAtDesc(AUTH_USER_ENTITY.getId());
		verify(quizRepository, times(1)).findByBriefingEntity(expectedBriefingEntity);
		Assertions.assertThat(preparedAlarmContents.quizVoiceUrl()).hasSize(expectedQuizzes.size());
		Assertions.assertThat(preparedAlarmContents.repeatFrequency())
			.isEqualTo(expectedAlarmEntity.getRepeatFrequency());
		Assertions.assertThat(preparedAlarmContents.repeatInterval())
			.isEqualTo(expectedAlarmEntity.getRepeatInterval());
		Assertions.assertThat(preparedAlarmContents.wakeUpTime()).isEqualTo(expectedAlarmEntity.getWakeUpTime());
		Assertions.assertThat(preparedAlarmContents.briefingVoiceUrl()).isEqualTo(
			expectedBriefingEntity.getWakeUpBriefingContent().getFileUrl());
	}

	private List<QuizEntity> createFakeQuiz() {
		return Stream.generate(() -> new QuizEntity(
				GENERATOR.lorem().sentence(2),
				1,
				Stream.generate(() -> GENERATOR.lorem().word())
					.limit(QuizConstraint.OPTION_SIZE)
					.toList(), 1
			))
			.limit(QuizConstraint.TOTAL_QUIZ_SIZE)
			.map(quiz -> {
				ReflectionTestUtils.setField(quiz, "wakeUpQuizContent", new Content(
					GENERATOR.file().fileName(),
					GENERATOR.number().randomNumber(),
					FileType.QUIZ,
					String.join("/", GENERATOR.internet().domainName(), GENERATOR.file().fileName()),
					AccessAuthority.PRIVATE
				));
				return quiz;
			})
			.toList();
	}
}