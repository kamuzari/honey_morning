package com.sf.honeymorning.user.adapter.out.persistence.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.javafaker.Faker;
import com.sf.honeymorning.user.adapter.out.persistence.entity.UserEntity;
import com.sf.honeymorning.user.adapter.out.persistence.entity.UserRole;

class UserEntityTest {
	static final Faker DATE_GENERATOR = new Faker();

	@DisplayName("사용자의 최대 스트릭을 갱신할때, 이전보다 값과 같다면 갱신하지 않는다")
	@Test
	void testNotUpdateStreak() {
		//given
		int consecutiveDays = 1;
		UserEntity userEntity = new UserEntity(
			DATE_GENERATOR.name().username(),
			DATE_GENERATOR.internet().password(8, 22),
			DATE_GENERATOR.name().title(),
			UserRole.ROLE_USER
		);
		//when
		userEntity.updateMaximumStreak(consecutiveDays);
		//then
		assertThat(userEntity.getMaxStreak()).isEqualTo(1);
	}

	@DisplayName("사용자의 최대 스트릭을 갱신할때, 이전 보다 크면 갱신한다")
	@Test
	void testUpdateStreak() {
		//given
		int newConsecutiveDays = 4;
		UserEntity userEntity = new UserEntity(
			DATE_GENERATOR.name().username(),
			DATE_GENERATOR.internet().password(8, 22),
			DATE_GENERATOR.name().title(),
			UserRole.ROLE_USER
		);
		ReflectionTestUtils.setField(userEntity, "maximumStreak", 3);

		//when
		userEntity.updateMaximumStreak(newConsecutiveDays);

		//then
		assertThat(userEntity.getMaxStreak()).isEqualTo(newConsecutiveDays);
	}

	@DisplayName("사용자의 최대 스트릭을 갱신할때, 이전보다 작으면 갱신하지 않는다")
	@Test
	void testNotUpdateStreakEqualStreakCount() {
		//given
		int consecutiveDays = 2;
		UserEntity userEntity = new UserEntity(
			DATE_GENERATOR.name().username(),
			DATE_GENERATOR.internet().password(8, 22),
			DATE_GENERATOR.name().title(),
			UserRole.ROLE_USER
		);
		int alreadyExistedMaxStreak = 3;
		ReflectionTestUtils.setField(userEntity, "maximumStreak", alreadyExistedMaxStreak);
		//when
		userEntity.updateMaximumStreak(consecutiveDays);
		//then
		assertThat(userEntity.getMaxStreak()).isEqualTo(alreadyExistedMaxStreak);
	}

}