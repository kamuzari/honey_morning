package com.sf.honeymorning.alarm.application.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AddAlarmResultTest {
	@DisplayName("match count 가 범위를 벗어나면 객체를 생성할 수 없다")
	@ParameterizedTest(name = "matchCount : {0}")
	@ValueSource(ints = {-1, 3, 4})
	void failInvalidMatchCount(int matchCount) {
		//given
		long userId = 1L;
		long briefingId = 1L;
		boolean attendance = true;
		//when
		//then
		assertThatThrownBy(() -> new AddAlarmResult(userId, briefingId, matchCount, attendance))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("참석 여부가 false인데 match count 가 0보다 크면 객체를 생성할 수 없다")
	@ParameterizedTest(name = "matchCount : {0}")
	@ValueSource(ints = {1, 2})
	void failInvalidMatchCountAndAbsence(int matchCount) {
		//given
		long userId = 1L;
		long briefingId = 1L;
		boolean absence = false;
		//when
		//then
		assertThatThrownBy(() -> new AddAlarmResult(userId, briefingId, matchCount, absence))
			.isInstanceOf(IllegalStateException.class);
	}
}