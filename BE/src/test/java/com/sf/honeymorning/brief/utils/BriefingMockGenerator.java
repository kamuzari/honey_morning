package com.sf.honeymorning.brief.utils;

import static com.sf.honeymorning.brief.common.TopicWordConstraint.SECTION_MAXIMUM_SIZE;
import static com.sf.honeymorning.brief.common.TopicWordConstraint.SECTION_MINIMUM_SIZE;
import static com.sf.honeymorning.brief.common.TopicWordConstraint.TOPIC_WORD_TOTAL_SIZE;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javafaker.Faker;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingTagEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.QuizEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.TopicModelWordEntity;
import com.sf.honeymorning.brief.common.QuizConstraint;
import com.sf.honeymorning.user.adapter.out.persistence.entity.UserEntity;
import com.sf.honeymorning.user.adapter.out.persistence.entity.UserRole;

// todo: 여기저기 흩어져있는 MockDataGenerator를 모아놓는 클래스
public class BriefingMockGenerator {
	public static final Faker GENERATOR = new Faker();

	public static Set<TopicModelWordEntity> createTopicModelWords() {
		return Stream.generate(() -> new TopicModelWordEntity(
				GENERATOR.number().numberBetween(SECTION_MINIMUM_SIZE, SECTION_MAXIMUM_SIZE),
				GENERATOR.lorem().word(),
				GENERATOR.number().randomDouble(2, 0, 100)))
			.limit(TOPIC_WORD_TOTAL_SIZE).collect(Collectors.toSet());
	}

	public static Set<QuizEntity> createQuizzes() {
		return Stream.generate(() -> new QuizEntity(
				GENERATOR.lorem().sentence(),
				GENERATOR.number().numberBetween(1, 4),
				List.of(GENERATOR.lorem().word(),
					GENERATOR.lorem().word(),
					GENERATOR.lorem().word(),
					GENERATOR.lorem().word())))
			.limit(QuizConstraint.TOTAL_QUIZ_SIZE).collect(Collectors.toSet());
	}

	public static BriefingEntity createBriefing(Long userId) {
		return new BriefingEntity(
			userId,
			GENERATOR.lorem().sentence(5),
			GENERATOR.lorem().sentence(30),
			GENERATOR.internet().url(),
			Set.of(new BriefingTagEntity("경제")),
			createQuizzes(),
			createTopicModelWords()
		);
	}

	public static UserEntity createUser() {
		return new UserEntity(
			GENERATOR.internet().emailAddress(),
			"",
			GENERATOR.name().username(),
			UserRole.ROLE_USER
		);
	}
}
