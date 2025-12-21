package com.honeymorning.api.brief.application.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EmptySelectionQuizTest {

	@DisplayName("답안이 [1-4] 사이의 숫자가 아니면 도메인 객체 생성시 예외가 발생한다")
	@ParameterizedTest(name = "{index} => selection={0}")
	@ValueSource(ints = {0, 5})
	void failInvalidSelection(int invalidSelection) {
		//given
		var emptySelectionQuiz = new EmptySelectionQuiz(1L, null);
		//when
		//then
		Assertions.assertThatThrownBy(() ->
			emptySelectionQuiz.solve(invalidSelection)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("답안이 기입된 상태로 객체를 생성하면 예외가 발생한다")
	@ParameterizedTest(name = "{index} => selection={0}")
	@ValueSource(ints = {0, 1, 2, 3, 4, 5})
	void failCreate(int notNullSelection) {
		//given

		//when
		//then
		Assertions.assertThatThrownBy(() ->
			new EmptySelectionQuiz(1L, notNullSelection)
		).isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("답안이 기입되지 않는 상태로 답안을 가져오려하면 예외가 발생한다")
	@Test
	void failNotFilledSelectionGet() {
		//given
		var emptySelectionQuiz = new EmptySelectionQuiz(1L, null);
		//when
		//then
		Assertions.assertThatThrownBy(emptySelectionQuiz::getSelection)
			.isInstanceOf(IllegalStateException.class);
	}

}