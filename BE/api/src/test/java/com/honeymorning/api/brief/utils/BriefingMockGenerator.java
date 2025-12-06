package com.honeymorning.api.brief.utils;

import static com.honeymorning.common.domain.briefing.constraint.QuizConstraint.TOTAL_QUIZ_SIZE;
import static com.honeymorning.common.domain.briefing.constraint.TopicWordConstraint.SECTION_MAXIMUM_SIZE;
import static com.honeymorning.common.domain.briefing.constraint.TopicWordConstraint.SECTION_MINIMUM_SIZE;
import static com.honeymorning.common.domain.briefing.constraint.TopicWordConstraint.TOPIC_WORD_TOTAL_SIZE;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.javafaker.Faker;
import com.honeymorning.common.domain.briefing.entity.BriefingEntity;
import com.honeymorning.common.domain.briefing.entity.BriefingTagEntity;
import com.honeymorning.common.domain.briefing.entity.QuizEntity;
import com.honeymorning.common.domain.briefing.entity.TopicModelWordEntity;
import com.honeymorning.common.domain.user.entity.UserEntity;
import com.honeymorning.common.domain.user.entity.UserRole;

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
		return IntStream.rangeClosed(1, TOTAL_QUIZ_SIZE).mapToObj(order -> new QuizEntity(
				GENERATOR.lorem().sentence(),
				GENERATOR.number().numberBetween(1, 4),
				List.of(GENERATOR.lorem().word(),
					GENERATOR.lorem().word(),
					GENERATOR.lorem().word(),
					GENERATOR.lorem().word()),
				order))
			.collect(Collectors.toSet());
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
