package com.honeymorning.relay;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.honeymorning.common.common.basic.EventStatus;
import com.honeymorning.common.domain.briefing.entity.BriefingEntity;
import com.honeymorning.common.domain.briefing.repository.BriefingRepository;
import com.honeymorning.relay.briefing.adapter.out.search.repository.BriefingContentRepository;
import com.honeymorning.relay.briefing.application.port.in.SearchDocumentFailCompensateUseCase;
import com.honeymorning.relay.context.infra.database.MySqlContext;
import com.honeymorning.relay.context.infra.integration.DefaultIntegrationTest;
import com.honeymorning.relay.event.entity.FailSearchEventEntity;
import com.honeymorning.relay.event.repository.FailSearchEventEntityRepository;

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