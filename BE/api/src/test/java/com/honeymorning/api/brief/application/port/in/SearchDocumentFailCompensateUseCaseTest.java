package com.honeymorning.api.brief.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.honeymorning.api.brief.utils.BriefingMockGenerator;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.event.FailSearchEventEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.event.FailSearchEventEntityRepository;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.BriefingRepository;
import com.sf.honeymorning.brief.adapter.out.search.BriefingContentRepository;
import com.sf.honeymorning.common.entity.basic.EventStatus;
import com.honeymorning.api.context.infra.database.MySqlContext;
import com.honeymorning.api.context.integration.DefaultIntegrationTest;

class SearchDocumentFailCompensateUseCaseTest extends DefaultIntegrationTest implements MySqlContext {
	@Autowired
	SearchDocumentFailCompensateUseCase sut;

	@Autowired
	FailSearchEventEntityRepository failSearchEventEntityRepository;

	@Autowired
	BriefingRepository briefingRepository;

	@Autowired
	BriefingContentRepository briefingContentRepository;

	@DisplayName("검색 데이터 반영")
	@Test
	void testRetrySearchDocument() {
		//given
		BriefingEntity briefing = BriefingMockGenerator.createBriefing(1L);
		briefingRepository.save(briefing);
		failSearchEventEntityRepository.save(new FailSearchEventEntity(briefing.getId()));
		//when
		sut.retrySearchDocument();
		//then
		var expectedStatusComplete = failSearchEventEntityRepository.findByBriefingId(briefing.getId())
			.orElseThrow();
		assertThat(expectedStatusComplete.getEventStatus()).isEqualTo(EventStatus.RETRY_COMPLETED);
		var savedDocument = briefingContentRepository.findBriefingContentDocumentByBriefingId(
				briefing.getId())
			.orElseThrow(() -> new AssertionError("검색 데이터 반영이 안되었습니다."));
		assertThat(savedDocument.getBriefingId()).isEqualTo(briefing.getId());
	}

}