package com.honeymorning.api.brief.adapter.out.persistence.entity;

import static com.sf.honeymorning.brief.common.QuizConstraint.ANSWER_MAXIMUM_VALUE;
import static com.sf.honeymorning.brief.common.QuizConstraint.ANSWER_MINIMUM_VALUE;
import static com.sf.honeymorning.brief.common.QuizConstraint.OPTION_SIZE;
import static com.honeymorning.api.brief.utils.BriefingMockGenerator.GENERATOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class QuizEntityTest {

	@DisplayName("퀴즈 객체를 생성한다")
	@Test
	void testCreateQuiz() {
		//given
		String problem = GENERATOR.lorem().sentence(4);
		int answer = GENERATOR.number().numberBetween(ANSWER_MINIMUM_VALUE, ANSWER_MAXIMUM_VALUE);
		List<String> options = Stream.generate(() -> GENERATOR.lorem().word()).limit(OPTION_SIZE).toList();

		//when
		QuizEntity quizEntity = createQuiz(problem, answer, options);

		//then
		assertThat(quizEntity.getProblem()).isEqualTo(problem);
		assertThat(quizEntity.getAnswer()).isEqualTo(answer);
		assertThat(quizEntity.getOption1()).isEqualTo(options.get(0));
		assertThat(quizEntity.getOption2()).isEqualTo(options.get(1));
		assertThat(quizEntity.getOption3()).isEqualTo(options.get(2));
		assertThat(quizEntity.getOption4()).isEqualTo(options.get(3));
		assertThat(quizEntity.getWakeUpQuizContent()).isNull();
		assertThat(quizEntity.getSelection()).isNull();
	}

	@DisplayName("퀴즈 객체 생성에 실패한다")
	@Nested
	class FailCreateQuizEntity {

		@DisplayName("problem 이 null 이거나 공백이라면 생성에 실패한다")
		@ParameterizedTest(name = "problem : {0}")
		@NullAndEmptySource
		void invalidProblem(String problem) {
			//given
			int answer = GENERATOR.number().numberBetween(ANSWER_MINIMUM_VALUE, ANSWER_MAXIMUM_VALUE);
			List<String> options = Stream.generate(() -> GENERATOR.lorem().word()).limit(4).toList();

			//when
			//then
			assertThatThrownBy(() -> createQuiz(problem, answer, options))
				.isInstanceOf(IllegalArgumentException.class);
		}

		@DisplayName("answer 이 [1-4] 범위를 벗어나면 생성에 실패한다")
		@ParameterizedTest(name = "answer : {0}")
		@ValueSource(ints = {0, -1, 5})
		void invalidAnswer(int answer) {
			//given
			String problem = GENERATOR.lorem().sentence(4);
			List<String> options = Stream.generate(() -> GENERATOR.lorem().word()).limit(OPTION_SIZE).toList();

			//when
			//then
			assertThatThrownBy(() -> createQuiz(problem, answer, options))
				.isInstanceOf(IllegalArgumentException.class);
		}

		@DisplayName("옵셥의 크기가 4가 아니면 생성에 실패한다")
		@ParameterizedTest(name = "option size : {0}")
		@ValueSource(ints = {0, 5, 6})
		void invalidOptionSize(int optionSize) {
			//given
			String problem = GENERATOR.lorem().sentence(4);
			int answer = GENERATOR.number().numberBetween(ANSWER_MINIMUM_VALUE, ANSWER_MAXIMUM_VALUE);
			List<String> options = Stream.generate(() -> GENERATOR.lorem().word()).limit(optionSize).toList();

			//when
			//then
			assertThatThrownBy(() -> createQuiz(problem, answer, options))
				.isInstanceOf(IllegalArgumentException.class);
		}
	}

	@DisplayName("퀴즈 답안을 기입할때, [1-4] 범위를 벗어나면 예외가 발생한다")
	@ParameterizedTest(name = "selection : {0}")
	@ValueSource(ints = {-1, 0, 5, 6})
	void failAddSelection(int invalidSelection) {
		//given
		String problem = GENERATOR.lorem().sentence(4);
		int answer = GENERATOR.number().numberBetween(ANSWER_MINIMUM_VALUE, ANSWER_MAXIMUM_VALUE);
		List<String> options = Stream.generate(() -> GENERATOR.lorem().word()).limit(4).toList();
		//when
		QuizEntity quizEntity = createQuiz(problem, answer, options);
		//then
		assertThatThrownBy(()-> quizEntity.addSelection(invalidSelection))
			.isInstanceOf(IllegalArgumentException.class);
	}

	QuizEntity createQuiz(String problem, int answer, List<String> options) {
		return new QuizEntity(problem, answer, options);
	}

}