package com.sf.honeymorning.brief.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.javafaker.Faker;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.entity.BriefingTag;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.brief.repository.BriefingTagRepository;
import com.sf.honeymorning.brief.service.BriefService;
import com.sf.honeymorning.context.ServiceIntegrationTest;
import com.sf.honeymorning.quiz.entity.Quiz;
import com.sf.honeymorning.quiz.repository.QuizRepository;
import com.sf.honeymorning.tag.entity.Tag;
import com.sf.honeymorning.tag.repository.TagRepository;

class BriefingIntegrationTest extends ServiceIntegrationTest {

	protected static final Faker FAKE_DATA_FACTORY = new Faker();

	@Autowired
	BriefService briefService;

	@Autowired
	BriefingRepository briefingRepository;

	@Autowired
	BriefingTagRepository briefingTagRepository;

	@Autowired
	QuizRepository quizRepository;

	@Autowired
	TagRepository tagRepository;

	@DisplayName("나의 브리핑 기록들을 가져온다. with pagination")
	@Test
	void get_my_briefing_histories() {
		//given
		Long authUserid = 1L;
		var pageSampleResponse = createPagingSampleData(authUserid);
		int initialPageNumber = 1;

		//when
		var myBriefings = briefService.getMyBriefings(authUserid, initialPageNumber);

		//then
		assertThat(myBriefings).isNotNull();
		assertThat(myBriefings.getMyBriefings()).isNotNull();
		assertThat(myBriefings.getMyBriefings()).hasSize(pageSampleResponse.contentTotalSize);
		assertThat(myBriefings.getTotalPage()).isEqualTo(pageSampleResponse.expectedTotalPage);
	}

	@DisplayName("나의 브리핑 기록들이 없어도 응답한다. with pagination")
	@Test
	void get_my_briefing_histories_empty() {
		//given
		Long authUserid = 2L;
		int initialPageNumber = 1;
		//when
		var myBriefings = briefService.getMyBriefings(authUserid, initialPageNumber);
		//then
		assertThat(myBriefings).isNotNull();
		assertThat(myBriefings.getTotalPage()).isZero();
	}

	PageSampleResponse createPagingSampleData(Long authUserId) {
		Briefing briefing = briefingRepository.save(new Briefing(
			authUserId,
			FAKE_DATA_FACTORY.lorem().sentence(3),
			FAKE_DATA_FACTORY.lorem().sentence(3),
			FAKE_DATA_FACTORY.internet().url()
		));

		Tag tag = tagRepository.save(
			new Tag(
				"경제"
			));

		briefingTagRepository.save(new BriefingTag(briefing, tag));
		quizRepository.saveAll(List.of(
			new Quiz(briefing,
				FAKE_DATA_FACTORY.friends().quote(),
				FAKE_DATA_FACTORY.number().numberBetween(1, 4),
				Stream.generate(() -> FAKE_DATA_FACTORY.lorem().sentence()).limit(4).toList(),
				FAKE_DATA_FACTORY.internet().url()),
			new Quiz(briefing,
				FAKE_DATA_FACTORY.friends().quote(),
				FAKE_DATA_FACTORY.number().numberBetween(1, 4),
				Stream.generate(() -> FAKE_DATA_FACTORY.lorem().sentence()).limit(4).toList(),
				FAKE_DATA_FACTORY.internet().url())
		));

		return new PageSampleResponse(1, 1);
	}

	record PageSampleResponse(
		int expectedTotalPage,
		int contentTotalSize
	) {

	}

}