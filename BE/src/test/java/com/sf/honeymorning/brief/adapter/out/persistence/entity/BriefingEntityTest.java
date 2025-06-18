package com.sf.honeymorning.brief.adapter.out.persistence.entity;

import static com.sf.honeymorning.brief.common.TopicWordConstraint.SECTION_MAXIMUM_SIZE;
import static com.sf.honeymorning.brief.common.TopicWordConstraint.SECTION_MINIMUM_SIZE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.javafaker.Faker;
import com.sf.honeymorning.common.entity.content.AccessAuthority;
import com.sf.honeymorning.common.entity.content.Content;
import com.sf.honeymorning.common.entity.content.FileType;
import com.sf.honeymorning.brief.common.QuizConstraint;

class BriefingEntityTest {
	static final Faker DATE_GENERATOR = new Faker();

	@Test
	@DisplayName("브리핑만 단독적으로 객체를 생성한다")
	void testCreateSingleBriefing() {
		//given
		long userId = 1L;
		//when
		BriefingEntity briefingEntity = new BriefingEntity(
			userId,
			DATE_GENERATOR.lorem().sentence(10),
			DATE_GENERATOR.lorem().sentence(40),
			DATE_GENERATOR.internet().domainName()
		);

		assertThat(briefingEntity.getUserId()).isEqualTo(userId);
		assertThat(briefingEntity.getSummaryText()).isEqualTo(briefingEntity.getSummaryText());
		assertThat(briefingEntity.getText()).isEqualTo(briefingEntity.getText());
		assertThat(briefingEntity.getWakeUpCallPath()).isEqualTo(briefingEntity.getWakeUpCallPath());
		assertThat(briefingEntity.getWakeUpBriefingContent()).isNull();
		assertThat(briefingEntity.getQuizEntities()).isNull();
		assertThat(briefingEntity.getBriefingTagEntities()).isNull();
		assertThat(briefingEntity.getTopicModelWordEntities()).isNull();
	}

	@Test
	@DisplayName("브리핑 관련 객체를 포함하여 생성할때, 모두 같은 브리핑을 참조한다")
	void testCreateBriefingWithSubDomain() {
		//given
		long userId = 1L;
		List<QuizEntity> createdQuizzes = createQuizzes();
		List<TopicModelWordEntity> createdTopicModels = createTopicModelWords();
		List<BriefingTagEntity> briefingTagEntities = List.of(new BriefingTagEntity(DATE_GENERATOR.lorem().word()));
		//when
		BriefingEntity briefingEntity = new BriefingEntity(
			userId,
			DATE_GENERATOR.lorem().sentence(10),
			DATE_GENERATOR.lorem().sentence(40),
			DATE_GENERATOR.internet().domainName(),
			briefingTagEntities,
			createdQuizzes,
			createdTopicModels
		);

		//then
		assertThat(createdQuizzes).extracting(QuizEntity::getBriefingEntity).containsOnly(briefingEntity);
		assertThat(briefingTagEntities).extracting(BriefingTagEntity::getBriefingEntity).containsOnly(briefingEntity);
		assertThat(createdTopicModels).extracting(TopicModelWordEntity::getBriefingEntity).containsOnly(briefingEntity);
	}

	@Test
	@DisplayName("브리핑 객체 생성 후에, summaryText 기반 Content를 추가할 수 있다.")
	void testAddContent(){
		//given
		long userId = 1L;
		BriefingEntity briefingEntity = new BriefingEntity(
			userId,
			DATE_GENERATOR.lorem().sentence(10),
			DATE_GENERATOR.lorem().sentence(40),
			DATE_GENERATOR.internet().domainName()
		);

		Content addingContent = new Content(
			DATE_GENERATOR.internet().domainName(),
			(long)DATE_GENERATOR.number().numberBetween(1000, 100_000),
			FileType.BRIEFING,
			DATE_GENERATOR.internet().url().toLowerCase(),
			AccessAuthority.PART_ALLOWED
		);

		//when
		briefingEntity.addWakeUpBriefingContent(addingContent);

		//then
		assertThat(briefingEntity.getWakeUpBriefingContent()).isEqualTo(addingContent);
	}

	@Test
	@DisplayName("브리핑 객체에 퀴즈 객체들이 존재하면 true를 반환한다")
	void testTrueIsEmptyQuizzes(){
		//given
		long userId = 1L;
		BriefingEntity briefingEntity = new BriefingEntity(
			userId,
			DATE_GENERATOR.lorem().sentence(10),
			DATE_GENERATOR.lorem().sentence(40),
			DATE_GENERATOR.internet().domainName()
		);


		//when
		boolean isEmptyQuizzes = briefingEntity.isEmptyQuizzes();
		//then
		assertThat(isEmptyQuizzes).isTrue();
	}

	@Test
	@DisplayName("브리핑 객체에 퀴즈 객체들이 존재지 않으면 false를 반환한다")
	void testFalseIsEmptyQuizzes(){
		long userId = 1L;
		List<QuizEntity> createdQuizzes = createQuizzes();
		List<TopicModelWordEntity> createdTopicModels = createTopicModelWords();
		List<BriefingTagEntity> briefingTagEntities = List.of(new BriefingTagEntity(DATE_GENERATOR.lorem().word()));
		BriefingEntity briefingEntity = new BriefingEntity(
			userId,
			DATE_GENERATOR.lorem().sentence(10),
			DATE_GENERATOR.lorem().sentence(40),
			DATE_GENERATOR.internet().domainName(),
			briefingTagEntities,
			createdQuizzes,
			createdTopicModels
		);

		//when
		boolean isEmptyQuizzes = briefingEntity.isEmptyQuizzes();
		//then
		assertThat(isEmptyQuizzes).isFalse();
	}

	List<QuizEntity> createQuizzes() {
		return Stream.generate(() -> new QuizEntity(
				DATE_GENERATOR.lorem().sentence(2),
				1,
				Stream.generate(() -> DATE_GENERATOR.lorem().word())
					.limit(QuizConstraint.OPTION_SIZE)
					.toList()
			)).limit(2)
			.toList();
	}

	List<TopicModelWordEntity> createTopicModelWords() {
		return Stream.generate(() -> new TopicModelWordEntity(
				DATE_GENERATOR.number().numberBetween(SECTION_MINIMUM_SIZE, SECTION_MAXIMUM_SIZE),
				DATE_GENERATOR.lorem().word(),
				DATE_GENERATOR.number().randomDouble(2, 0, 100)))
			.limit(150).toList();
	}

}