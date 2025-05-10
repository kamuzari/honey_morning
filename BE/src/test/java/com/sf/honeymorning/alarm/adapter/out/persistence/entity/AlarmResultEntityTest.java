package com.sf.honeymorning.alarm.adapter.out.persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.sf.honeymorning.alarm.adapter.out.persistence.entity.AlarmResultEntity;

class AlarmResultEntityTest {

	@Test
	@DisplayName("알람 결과 객체를 생성한다")
	void createAlarmResult() {
		//given
		long userId = 1L;
		long briefingId = 1L;
		int matchCount = 1;
		boolean attendance = true;
		//when
		AlarmResultEntity alarmResultEntity = new AlarmResultEntity(userId, briefingId, matchCount, attendance);
		//then
		assertThat(alarmResultEntity.getUserId()).isEqualTo(userId);
		assertThat(alarmResultEntity.getBriefingId()).isEqualTo(briefingId);
		assertThat(alarmResultEntity.getCount()).isEqualTo(matchCount);
		assertThat(alarmResultEntity.isAttended()).isEqualTo(attendance);
	}

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
		assertThatThrownBy(() -> new AlarmResultEntity(userId, briefingId, matchCount, attendance))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("참석 여부가 false인데 match count 가 0보다 크면 객체를 생성할 수 없다")
	@ParameterizedTest(name = "matchCount : {0}")
	@ValueSource(ints = {1,2})
	void failInvalidMatchCountAndAbsence(int matchCount) {
		//given
		long userId = 1L;
		long briefingId = 1L;
		boolean absence = false;
		//when
		//then
		assertThatThrownBy(() -> new AlarmResultEntity(userId, briefingId, matchCount, absence))
			.isInstanceOf(IllegalStateException.class);
	}

}