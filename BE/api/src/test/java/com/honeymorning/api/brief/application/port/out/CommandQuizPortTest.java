package com.honeymorning.api.brief.application.port.out;

import static com.honeymorning.api.brief.utils.BriefingMockGenerator.GENERATOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;

import com.honeymorning.api.brief.adapter.out.persistence.QuizPersistenceAdapter;
import com.honeymorning.api.brief.adapter.out.persistence.entity.QuizEntity;
import com.honeymorning.api.brief.adapter.out.persistence.mapper.QuizPersistenceMapper;
import com.honeymorning.api.brief.adapter.out.persistence.repository.BriefingRepository;
import com.honeymorning.api.brief.adapter.out.persistence.repository.QuizRepository;
import com.honeymorning.api.brief.application.domain.EmptySelectionQuiz;
import com.honeymorning.api.brief.common.QuizConstraint;
import com.honeymorning.api.context.mock.MockTest;
import com.honeymorning.common.exception.NotFoundResourceException;

public class CommandQuizPortTest extends MockTest {
	CommandQuizPort sut;

	@InjectMocks
	QuizPersistenceAdapter quizPersistenceAdapter;

	@Mock
	QuizRepository quizRepository;

	@Mock
	BriefingRepository briefingRepository;

	@Spy
	QuizPersistenceMapper quizPersistenceMapper;

	@BeforeEach
	void setUp() {
		this.sut = quizPersistenceAdapter;
	}

	@DisplayName("사용자가 선택한 답안들이 반영된다")
	@Test
	void testReflect() {
		//given
		List<QuizEntity> quizEntities = LongStream.rangeClosed(1, 2).mapToObj((quizId) -> {
			QuizEntity quizEntity = new QuizEntity(
				GENERATOR.lorem().sentence(3),
				1,
				Stream.generate(() -> GENERATOR.lorem().word()).limit(QuizConstraint.OPTION_SIZE).toList());
			ReflectionTestUtils.setField(quizEntity, "id", quizId);

			return quizEntity;
		}).toList();

		List<EmptySelectionQuiz> filledSelectionQuizzes = List.of(
			new EmptySelectionQuiz(1L, null),
			new EmptySelectionQuiz(2L, null)
		);
		filledSelectionQuizzes.forEach(emptySelectionQuiz -> emptySelectionQuiz.solve(1));

		given(quizRepository.findAllById(any())).willReturn(quizEntities);

		//when
		sut.reflect(filledSelectionQuizzes);

		//then
		quizEntities.forEach(quizEntity -> {
			assertThat(quizEntity.getSelection()).isNotNull();
		});

		quizEntities.forEach(quizEntity -> {
			var solvedQuiz = filledSelectionQuizzes.stream()
				.filter(filledQuiz -> filledQuiz.getQuizId().equals(quizEntity.getId()))
				.findAny()
				.orElseThrow(RuntimeException::new);
			assertThat(quizEntity.getSelection()).isEqualTo(solvedQuiz.getSelection());
		});
	}

	@DisplayName("사용자가 요청한 quizId가 잘못되면 예외가 발생한다")
	@Test
	void failInvalidQuizId() {
		//given
		List<QuizEntity> quizEntities = LongStream.rangeClosed(1, 2).mapToObj((quizId) -> {
			QuizEntity quizEntity = new QuizEntity(
				GENERATOR.lorem().sentence(3),
				1,
				Stream.generate(() -> GENERATOR.lorem().word()).limit(QuizConstraint.OPTION_SIZE).toList());
			ReflectionTestUtils.setField(quizEntity, "id", quizId);

			return quizEntity;
		}).toList();

		List<EmptySelectionQuiz> invalidFilledQuizzes = List.of(
			new EmptySelectionQuiz(3L, null),
			new EmptySelectionQuiz(4L, null)
		);
		invalidFilledQuizzes.forEach(emptySelectionQuiz -> emptySelectionQuiz.solve(1));

		given(quizRepository.findAllById(any())).willReturn(quizEntities);

		//when
		//then
		Assertions.assertThatThrownBy(() ->
				sut.reflect(invalidFilledQuizzes))
			.isInstanceOf(NotFoundResourceException.class);
	}
}
