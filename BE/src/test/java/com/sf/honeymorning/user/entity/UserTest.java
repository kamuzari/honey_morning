package com.sf.honeymorning.user.entity;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {
    static final Faker DATE_GENERATOR = new Faker();

    @DisplayName("사용자의 최대 스트릭을 갱신할때, 이전보다 값과 같다면 갱신하지 않는다")
    @Test
    void testNotUpdateStreak() {
        //given
        int consecutiveDays = 1;
        User user = new User(
                DATE_GENERATOR.name().username(),
                DATE_GENERATOR.internet().password(8, 22),
                DATE_GENERATOR.name().title(),
                UserRole.ROLE_USER
        );
        //when
        user.updateMaximumStreak(consecutiveDays);
        //then
        assertThat(user.getMaxStreak()).isEqualTo(1);
    }

    @DisplayName("사용자의 최대 스트릭을 갱신할때, 이전 보다 크면 갱신한다")
    @Test
    void testUpdateStreak() {
        //given
        int newConsecutiveDays = 4;
        User user = new User(
                DATE_GENERATOR.name().username(),
                DATE_GENERATOR.internet().password(8, 22),
                DATE_GENERATOR.name().title(),
                UserRole.ROLE_USER
        );
        ReflectionTestUtils.setField(user, "maximumStreak", 3);

        //when
        user.updateMaximumStreak(newConsecutiveDays);

        //then
        assertThat(user.getMaxStreak()).isEqualTo(newConsecutiveDays);
    }

    @DisplayName("사용자의 최대 스트릭을 갱신할때, 이전보다 작으면 갱신하지 않는다")
    @Test
    void testNotUpdateStreakEqualStreakCount() {
        //given
        int consecutiveDays = 2;
        User user = new User(
                DATE_GENERATOR.name().username(),
                DATE_GENERATOR.internet().password(8, 22),
                DATE_GENERATOR.name().title(),
                UserRole.ROLE_USER
        );
        int alreadyExistedMaxStreak = 3;
        ReflectionTestUtils.setField(user, "maximumStreak", alreadyExistedMaxStreak);
        //when
        user.updateMaximumStreak(consecutiveDays);
        //then
        assertThat(user.getMaxStreak()).isEqualTo(alreadyExistedMaxStreak);
    }

}