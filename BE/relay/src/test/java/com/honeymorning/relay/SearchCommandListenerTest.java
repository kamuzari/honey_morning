package com.honeymorning.relay;

import static com.honeymorning.relay.BriefingMockGenerator.GENERATOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;

import com.honeymorning.common.domain.briefing.entity.BriefingEntity;
import com.honeymorning.common.domain.briefing.repository.BriefingRepository;
import com.honeymorning.relay.briefing.adapter.in.event.SearchCommandListener;
import com.honeymorning.relay.briefing.adapter.in.event.dto.BriefingSearchCommandDto;
import com.honeymorning.relay.briefing.application.port.in.SearchCommandUseCase;
import com.honeymorning.relay.config.AsyncTestConfig;
import com.honeymorning.relay.context.infra.integration.DefaultIntegrationTest;
import com.honeymorning.relay.event.repository.FailSearchEventEntityRepository;

import jakarta.validation.ValidationException;

@Import(AsyncTestConfig.class)
class SearchCommandListenerTest extends DefaultIntegrationTest {

	@Autowired
	SearchCommandListener sut;

	@SpyBean
	SearchCommandUseCase searchCommandUseCase;

	@Autowired
	BriefingRepository briefingRepository;

	@Autowired
	FailSearchEventEntityRepository failSearchEventEntityRepository;

	@BeforeEach
	void setUp() {
		AsyncTestConfig.initialize();
	}

	@DisplayName("브리핑 아이디가 null이거나, 음수이면 validation 예외가 발생한다")
	@ParameterizedTest(name = "problem : {0}")
	@NullSource
	@ValueSource(longs = {-1, -2, 0})
	void testGenerate(Long invalidBriefingId) throws InterruptedException {
		//given
		//when
		sut.register(new BriefingSearchCommandDto(invalidBriefingId));

		// then
		boolean isError = AsyncTestConfig.errorLatch.await(3, TimeUnit.SECONDS);
		assertThat(isError).isTrue();
		assertThat(AsyncTestConfig.capturedError.get()).isInstanceOf(ValidationException.class);
	}

	@DisplayName("만약 비즈니스 처리 중 예외가 발생하면 fail_search_events 테이블에 저장된다")
	@Test
	void failGenerate() throws InterruptedException {
		//given
		BriefingEntity briefing = briefingRepository.save(new BriefingEntity(
			GENERATOR.number().randomNumber(),
			GENERATOR.lorem().sentence(),
			GENERATOR.lorem().sentences(10).stream().collect(Collectors.joining()),
			GENERATOR.file().fileName()
		));
		doThrow(new RuntimeException("알수 없는 예외")).when(searchCommandUseCase).register(any());

		//when
		sut.register(new BriefingSearchCommandDto(briefing.getId()));

		// then
		boolean isError = AsyncTestConfig.errorLatch.await(2, TimeUnit.SECONDS);
		assertThat(isError).isTrue();
		var failSearchEventEntity = failSearchEventEntityRepository.findByBriefingId(briefing.getId()).orElseThrow();
		assertThat(failSearchEventEntity.getBriefingId()).isEqualTo(briefing.getId());
	}
}