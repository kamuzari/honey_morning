package com.sf.honeymorning.brief.integration;

import static com.sf.honeymorning.brief.common.TopicWordConstraint.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.javafaker.Faker;
import com.sf.honeymorning.alarm.service.dto.response.AiQuizDto;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.entity.BriefingTag;
import com.sf.honeymorning.brief.entity.TopicModelWord;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.brief.service.BriefingService;
import com.sf.honeymorning.context.DefaultIntegrationTest;
import com.sf.honeymorning.brief.common.QuizConstraint;
import com.sf.honeymorning.brief.entity.Quiz;

class BriefingIntegrationTest extends DefaultIntegrationTest {

	protected static final Faker FAKE_DATA_FACTORY = new Faker();

	@Autowired
	BriefingService briefingService;

	@Autowired
	BriefingRepository briefingRepository;

	@DisplayName("나의 브리핑 기록들을 가져온다. with pagination")
	@Test
	void testGetMyBriefingHistories() {
		//given
		Long authUserid = 1L;
		var pageSampleResponse = createPagingSampleData(authUserid);
		int initialPageNumber = 1;

		//when
		var myBriefings = briefingService.getMyBriefings(authUserid, initialPageNumber);

		//then
		assertThat(myBriefings).isNotNull();
		assertThat(myBriefings.getMyBriefings()).isNotNull();
		assertThat(myBriefings.getMyBriefings()).hasSize(pageSampleResponse.contentTotalSize);
		assertThat(myBriefings.getTotalPage()).isEqualTo(pageSampleResponse.expectedTotalPage);
	}

	@DisplayName("나의 브리핑 기록들이 없어도 응답한다. with pagination")
	@Test
	void testMyEmptyBriefingHistories() {
		//given
		Long authUserid = 2L;
		int initialPageNumber = 1;
		//when
		var myBriefings = briefingService.getMyBriefings(authUserid, initialPageNumber);
		//then
		assertThat(myBriefings).isNotNull();
		assertThat(myBriefings.getTotalPage()).isZero();
	}

	PageSampleResponse createPagingSampleData(Long authUserId) {
		Briefing briefing = briefingRepository.save(new Briefing(
			authUserId,
			FAKE_DATA_FACTORY.lorem().sentence(3),
			FAKE_DATA_FACTORY.lorem().sentence(3),
			FAKE_DATA_FACTORY.internet().url(),
			List.of(new BriefingTag("경제")),
			List.of(
				new Quiz(
					FAKE_DATA_FACTORY.friends().quote(),
					FAKE_DATA_FACTORY.number().numberBetween(1, 4),
					Stream.generate(() -> FAKE_DATA_FACTORY.lorem().sentence()).limit(4).toList()
					),
				new Quiz(
					FAKE_DATA_FACTORY.friends().quote(),
					FAKE_DATA_FACTORY.number().numberBetween(1, 4),
					Stream.generate(() -> FAKE_DATA_FACTORY.lorem().sentence()).limit(4).toList())
			),
			Stream.generate(() -> new TopicModelWord(
					FAKE_DATA_FACTORY.number().numberBetween(SECTION_MINIMUM_SIZE, SECTION_MAXIMUM_SIZE),
					FAKE_DATA_FACTORY.lorem().word(),
					FAKE_DATA_FACTORY.number().randomDouble(2, 0, 100)))
				.limit(150).toList()
		));

		briefingRepository.save(briefing);

		return new PageSampleResponse(1, 1);
	}

	List<AiQuizDto> createFakeQuizDtos(int size) {
		return Stream.generate(() -> new AiQuizDto(
				FAKE_DATA_FACTORY.lorem().sentence(2),
				1,
				Stream.generate(() -> FAKE_DATA_FACTORY.lorem().word())
					.limit(QuizConstraint.OPTION_SIZE)
					.toList()
			))
			.limit(size)
			.toList();
	}

	record PageSampleResponse(
		int expectedTotalPage,
		int contentTotalSize
	) {

	}

}