package com.honeymorning.api.alarm.adapter.out.persistence.entity;

import static com.honeymorning.api.brief.utils.BriefingMockGenerator.GENERATOR;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;


class TagEntityTest {

	@DisplayName("태그 객체를 생성한다")
	@Test
	void testCreateTag() {
		//given
		String word = GENERATOR.lorem().word();

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
		assertThatThrownBy(() -> new TagEntity(word))
			.isInstanceOf(IllegalArgumentException.class);
	}

}