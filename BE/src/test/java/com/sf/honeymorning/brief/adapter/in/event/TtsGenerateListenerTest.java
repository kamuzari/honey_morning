package com.sf.honeymorning.brief.adapter.in.event;

import static com.sf.honeymorning.brief.utils.BriefingMockGenerator.GENERATOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.doThrow;

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

import com.sf.honeymorning.brief.adapter.in.event.dto.BriefingTtsCommandDto;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.event.FailTtsEventEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.event.FailTtsEventEntityRepository;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.BriefingRepository;
import com.sf.honeymorning.brief.application.port.in.TextToSpeechCommandUseCase;
import com.sf.honeymorning.common.config.AsyncTestConfig;
import com.sf.honeymorning.context.integration.DefaultIntegrationTest;

import jakarta.validation.ValidationException;

@Import(AsyncTestConfig.class)
class TtsGenerateListenerTest extends DefaultIntegrationTest {

	@Autowired
	TtsGenerateListener sut;

	@SpyBean
	TextToSpeechCommandUseCase textToSpeechCommandUseCase;

	@Autowired
	BriefingRepository briefingRepository;

	@Autowired
	FailTtsEventEntityRepository failTtsEventEntityRepository;

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
		sut.generate(new BriefingTtsCommandDto(invalidBriefingId));

		// then
		boolean isError = AsyncTestConfig.errorLatch.await(3, TimeUnit.SECONDS);
		assertThat(isError).isTrue();
		assertThat(AsyncTestConfig.capturedError.get()).isInstanceOf(ValidationException.class);
	}

	@DisplayName("만약 비즈니스 처리 중 예외가 발생하면 fail_tts_events 테이블에 저장된다")
	@Test
	void failGenerate() throws InterruptedException {
		//given
		BriefingEntity briefing = briefingRepository.save(new BriefingEntity(
			GENERATOR.number().randomNumber(),
			GENERATOR.lorem().sentence(),
			GENERATOR.lorem().sentences(10).stream().collect(Collectors.joining()),
			GENERATOR.file().fileName()
		));
		doThrow(new RuntimeException("알수 없는 예외")).when(textToSpeechCommandUseCase).create(any());

		//when
		sut.generate(new BriefingTtsCommandDto(briefing.getId()));

		// then
		boolean isError = AsyncTestConfig.errorLatch.await(2, TimeUnit.SECONDS);
		assertThat(isError).isTrue();
		var failTtsEventEntity = failTtsEventEntityRepository.findByBriefingId(briefing.getId()).orElseThrow();
		assertThat(failTtsEventEntity.getBriefingId()).isEqualTo(briefing.getId());
	}
}