package com.honeymorning.api.brief.adapter.in.web.port.out;

import static com.honeymorning.api.brief.utils.BriefingMockGenerator.GENERATOR;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.honeymorning.api.brief.utils.BriefingMockGenerator;
import com.honeymorning.api.context.integration.DefaultIntegrationTest;
import com.honeymorning.common.domain.briefing.entity.BriefingEntity;
import com.honeymorning.common.domain.briefing.entity.BriefingTagEntity;
import com.honeymorning.common.domain.briefing.repository.BriefingRepository;

class BriefingQueryPortIntegrationTest extends DefaultIntegrationTest {

	@Autowired
	BriefingQueryPort sut;

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
		var myBriefings = sut.getMyBriefings(authUserid, initialPageNumber);

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
		var myBriefings = sut.getMyBriefings(authUserid, initialPageNumber);
		//then
		assertThat(myBriefings).isNotNull();
		assertThat(myBriefings.getTotalPage()).isZero();
	}

	PageSampleResponse createPagingSampleData(Long authUserId) {
		briefingRepository.save(new BriefingEntity(
			authUserId,
			GENERATOR.lorem().sentence(3),
			GENERATOR.lorem().sentence(3),
			GENERATOR.internet().url(),
			Set.of(new BriefingTagEntity("경제")),
			BriefingMockGenerator.createQuizzes(),
			BriefingMockGenerator.createTopicModelWords()
		));

		return new PageSampleResponse(1, 1);
	}

	record PageSampleResponse(
		int expectedTotalPage,
		int contentTotalSize
	) {

	}

}