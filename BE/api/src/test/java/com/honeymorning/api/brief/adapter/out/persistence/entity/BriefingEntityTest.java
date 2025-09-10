package com.honeymorning.api.brief.adapter.out.persistence.entity;

import static com.honeymorning.api.brief.utils.BriefingMockGenerator.GENERATOR;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.honeymorning.api.brief.utils.BriefingMockGenerator;
import com.honeymorning.common.common.content.AccessAuthority;
import com.honeymorning.common.common.content.Content;
import com.honeymorning.common.common.content.FileType;

class BriefingEntityTest {

	@Test
	@DisplayName("브리핑만 단독적으로 객체를 생성한다")
	void testCreateSingleBriefing() {
		//given
		long userId = 1L;
		//when
		BriefingEntity briefingEntity = new BriefingEntity(
			userId,
			GENERATOR.lorem().sentence(10),
			GENERATOR.lorem().sentence(40),
			GENERATOR.internet().domainName()
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
		Set<QuizEntity> createdQuizzes = BriefingMockGenerator.createQuizzes();
		Set<TopicModelWordEntity> createdTopicModels = BriefingMockGenerator.createTopicModelWords();
		Set<BriefingTagEntity> briefingTagEntities = Set.of(new BriefingTagEntity(GENERATOR.lorem().word()));
		//when
		BriefingEntity briefingEntity = new BriefingEntity(
			userId,
			GENERATOR.lorem().sentence(10),
			GENERATOR.lorem().sentence(40),
			GENERATOR.internet().domainName(),
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
	void testAddContent() {
		//given
		long userId = 1L;
		BriefingEntity briefingEntity = new BriefingEntity(
			userId,
			GENERATOR.lorem().sentence(10),
			GENERATOR.lorem().sentence(40),
			GENERATOR.internet().domainName()
		);

		Content addingContent = new Content(
			GENERATOR.internet().domainName(),
			(long)GENERATOR.number().numberBetween(1000, 100_000),
			FileType.BRIEFING,
			GENERATOR.internet().url().toLowerCase(),
			AccessAuthority.PART_ALLOWED
		);

		//when
		briefingEntity.addWakeUpBriefingContent(addingContent);

		//then
		assertThat(briefingEntity.getWakeUpBriefingContent()).isEqualTo(addingContent);
	}

	@Test
	@DisplayName("브리핑 객체에 퀴즈 객체들이 존재하면 true를 반환한다")
	void testTrueIsEmptyQuizzes() {
		//given
		long userId = 1L;
		BriefingEntity briefingEntity = new BriefingEntity(
			userId,
			GENERATOR.lorem().sentence(10),
			GENERATOR.lorem().sentence(40),
			GENERATOR.internet().domainName()
		);

		//when
		boolean isEmptyQuizzes = briefingEntity.isEmptyQuizzes();
		//then
		assertThat(isEmptyQuizzes).isTrue();
	}

	@Test
	@DisplayName("브리핑 객체에 퀴즈 객체들이 존재지 않으면 false를 반환한다")
	void testFalseIsEmptyQuizzes() {
		long userId = 1L;
		Set<QuizEntity> createdQuizzes = BriefingMockGenerator.createQuizzes();
		Set<TopicModelWordEntity> createdTopicModels = BriefingMockGenerator.createTopicModelWords();
		Set<BriefingTagEntity> briefingTagEntities = Set.of(new BriefingTagEntity(GENERATOR.lorem().word()));
		BriefingEntity briefingEntity = new BriefingEntity(
			userId,
			GENERATOR.lorem().sentence(10),
			GENERATOR.lorem().sentence(40),
			GENERATOR.internet().domainName(),
			briefingTagEntities,
			createdQuizzes,
			createdTopicModels
		);

		//when
		boolean isEmptyQuizzes = briefingEntity.isEmptyQuizzes();
		//then
		assertThat(isEmptyQuizzes).isFalse();
	}
}