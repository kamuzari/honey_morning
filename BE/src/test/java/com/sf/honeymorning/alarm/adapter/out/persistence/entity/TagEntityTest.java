package com.sf.honeymorning.alarm.adapter.out.persistence.entity;

import com.github.javafaker.Faker;
import com.sf.honeymorning.alarm.adapter.out.persistence.entity.TagEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TagEntityTest {
    static final Faker DATE_GENERATOR = new Faker();

    @DisplayName("태그 객체를 생성한다")
    @Test
    void testCreateTag() {
        //given
        String word = DATE_GENERATOR.lorem().word();

        //when
        TagEntity tagEntity = new TagEntity(word);

        //then
        assertThat(tagEntity.getWord()).isEqualTo(word);
    }

    @DisplayName("word 속성이 null 이거나 공백으로 이루어진다면 객체를 생성할 수 없다")
    @ParameterizedTest(name = "name: {0}")
    @NullAndEmptySource
    void failCreateTag(String word) {
        //given
        //when
        //then
        assertThatThrownBy(()-> new TagEntity(word))
                .isInstanceOf(IllegalArgumentException.class);
    }

}