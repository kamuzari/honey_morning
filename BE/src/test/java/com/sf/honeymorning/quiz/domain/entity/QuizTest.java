package com.sf.honeymorning.quiz.domain.entity;

import static com.sf.honeymorning.quiz.common.QuizConstraint.MAXIMUM_VALUE;
import static com.sf.honeymorning.quiz.common.QuizConstraint.MINIMUM_VALUE;
import static com.sf.honeymorning.quiz.common.QuizConstraint.OPTION_SIZE;
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

import com.github.javafaker.Faker;

class QuizTest {
	static final Faker DATE_GENERATOR = new Faker();

	@DisplayName("퀴즈 객체를 생성한다")
	@Test
	void testCreateQuiz() {
		//given
		String problem = DATE_GENERATOR.lorem().sentence(4);
		int answer = DATE_GENERATOR.number().numberBetween(MINIMUM_VALUE, MAXIMUM_VALUE);
		List<String> options = Stream.generate(() -> DATE_GENERATOR.lorem().word()).limit(OPTION_SIZE).toList();

		//when
		Quiz quiz = createQuiz(problem, answer, options);

		//then
		assertThat(quiz.getProblem()).isEqualTo(problem);
		assertThat(quiz.getAnswer()).isEqualTo(answer);
		assertThat(quiz.getOption1()).isEqualTo(options.get(0));
		assertThat(quiz.getOption2()).isEqualTo(options.get(1));
		assertThat(quiz.getOption3()).isEqualTo(options.get(2));
		assertThat(quiz.getOption4()).isEqualTo(options.get(3));
		assertThat(quiz.getWakeUpQuizContent()).isNull();
		assertThat(quiz.getSelection()).isNull();
	}

	@DisplayName("퀴즈 객체 생성에 실패한다")
	@Nested
	class FailCreateQuiz {

		@DisplayName("problem 이 null 이거나 공백이라면 생성에 실패한다")
		@ParameterizedTest(name = "problem : {0}")
		@NullAndEmptySource
		void invalidProblem(String problem) {
			//given
			int answer = DATE_GENERATOR.number().numberBetween(MINIMUM_VALUE, MAXIMUM_VALUE);
			List<String> options = Stream.generate(() -> DATE_GENERATOR.lorem().word()).limit(4).toList();

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
			String problem = DATE_GENERATOR.lorem().sentence(4);
			List<String> options = Stream.generate(() -> DATE_GENERATOR.lorem().word()).limit(OPTION_SIZE).toList();

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
			String problem = DATE_GENERATOR.lorem().sentence(4);
			int answer = DATE_GENERATOR.number().numberBetween(MINIMUM_VALUE, MAXIMUM_VALUE);
			List<String> options = Stream.generate(() -> DATE_GENERATOR.lorem().word()).limit(optionSize).toList();

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
		String problem = DATE_GENERATOR.lorem().sentence(4);
		int answer = DATE_GENERATOR.number().numberBetween(MINIMUM_VALUE, MAXIMUM_VALUE);
		List<String> options = Stream.generate(() -> DATE_GENERATOR.lorem().word()).limit(4).toList();
		//when
		Quiz quiz = createQuiz(problem, answer, options);
		//then
		assertThatThrownBy(()-> quiz.addSelection(invalidSelection))
			.isInstanceOf(IllegalArgumentException.class);
	}

	Quiz createQuiz(String problem, int answer, List<String> options) {
		return new Quiz(problem, answer, options);
	}

}