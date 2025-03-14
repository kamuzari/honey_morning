package com.sf.honeymorning.brief.entity;

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

class BriefingTest {
	static final Faker DATE_GENERATOR = new Faker();

	@Test
	@DisplayName("브리핑만 단독적으로 객체를 생성한다")
	void testCreateSingleBriefing() {
		//given
		long userId = 1L;
		//when
		Briefing briefing = new Briefing(
			userId,
			DATE_GENERATOR.lorem().sentence(10),
			DATE_GENERATOR.lorem().sentence(40),
			DATE_GENERATOR.internet().domainName()
		);

		assertThat(briefing.getUserId()).isEqualTo(userId);
		assertThat(briefing.getSummaryText()).isEqualTo(briefing.getSummaryText());
		assertThat(briefing.getText()).isEqualTo(briefing.getText());
		assertThat(briefing.getWakeUpCallPath()).isEqualTo(briefing.getWakeUpCallPath());
		assertThat(briefing.getWakeUpBriefingContent()).isNull();
		assertThat(briefing.getQuizzes()).isNull();
		assertThat(briefing.getBriefingTags()).isNull();
		assertThat(briefing.getTopicModelWords()).isNull();
	}

	@Test
	@DisplayName("브리핑 관련 객체를 포함하여 생성할때, 모두 같은 브리핑을 참조한다")
	void testCreateBriefingWithSubDomain() {
		//given
		long userId = 1L;
		List<Quiz> createdQuizzes = createQuizzes();
		List<TopicModelWord> createdTopicModels = createTopicModelWords();
		List<BriefingTag> briefingTags = List.of(new BriefingTag(DATE_GENERATOR.lorem().word()));
		//when
		Briefing briefing = new Briefing(
			userId,
			DATE_GENERATOR.lorem().sentence(10),
			DATE_GENERATOR.lorem().sentence(40),
			DATE_GENERATOR.internet().domainName(),
			briefingTags,
			createdQuizzes,
			createdTopicModels
		);

		//then
		assertThat(createdQuizzes).extracting(Quiz::getBriefing).containsOnly(briefing);
		assertThat(briefingTags).extracting(BriefingTag::getBriefing).containsOnly(briefing);
		assertThat(createdTopicModels).extracting(TopicModelWord::getBriefing).containsOnly(briefing);
	}

	@Test
	@DisplayName("브리핑 객체 생성 후에, summaryText 기반 Content를 추가할 수 있다.")
	void testAddContent(){
		//given
		long userId = 1L;
		Briefing briefing = new Briefing(
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
		briefing.addWakeUpBriefingContent(addingContent);

		//then
		assertThat(briefing.getWakeUpBriefingContent()).isEqualTo(addingContent);
	}

	@Test
	@DisplayName("브리핑 객체에 퀴즈 객체들이 존재하면 true를 반환한다")
	void testTrueIsEmptyQuizzes(){
		//given
		long userId = 1L;
		Briefing briefing = new Briefing(
			userId,
			DATE_GENERATOR.lorem().sentence(10),
			DATE_GENERATOR.lorem().sentence(40),
			DATE_GENERATOR.internet().domainName()
		);


		//when
		boolean isEmptyQuizzes = briefing.isEmptyQuizzes();
		//then
		assertThat(isEmptyQuizzes).isTrue();
	}

	@Test
	@DisplayName("브리핑 객체에 퀴즈 객체들이 존재지 않으면 false를 반환한다")
	void testFalseIsEmptyQuizzes(){
		long userId = 1L;
		List<Quiz> createdQuizzes = createQuizzes();
		List<TopicModelWord> createdTopicModels = createTopicModelWords();
		List<BriefingTag> briefingTags = List.of(new BriefingTag(DATE_GENERATOR.lorem().word()));
		Briefing briefing = new Briefing(
			userId,
			DATE_GENERATOR.lorem().sentence(10),
			DATE_GENERATOR.lorem().sentence(40),
			DATE_GENERATOR.internet().domainName(),
			briefingTags,
			createdQuizzes,
			createdTopicModels
		);

		//when
		boolean isEmptyQuizzes = briefing.isEmptyQuizzes();
		//then
		assertThat(isEmptyQuizzes).isFalse();
	}

	List<Quiz> createQuizzes() {
		return Stream.generate(() -> new Quiz(
				DATE_GENERATOR.lorem().sentence(2),
				1,
				Stream.generate(() -> DATE_GENERATOR.lorem().word())
					.limit(QuizConstraint.OPTION_SIZE)
					.toList()
			)).limit(2)
			.toList();
	}

	List<TopicModelWord> createTopicModelWords() {
		return Stream.generate(() -> new TopicModelWord(
				DATE_GENERATOR.number().numberBetween(SECTION_MINIMUM_SIZE, SECTION_MAXIMUM_SIZE),
				DATE_GENERATOR.lorem().word(),
				DATE_GENERATOR.number().randomDouble(2, 0, 100)))
			.limit(150).toList();
	}

}