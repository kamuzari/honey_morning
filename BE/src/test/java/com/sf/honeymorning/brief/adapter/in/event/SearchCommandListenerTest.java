package com.sf.honeymorning.brief.adapter.in.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.sf.honeymorning.brief.adapter.in.event.dto.BriefingSearchCommandDto;
import com.sf.honeymorning.common.config.AsyncTestConfig;
import com.sf.honeymorning.context.integration.DefaultIntegrationTest;

import jakarta.validation.ValidationException;

@Import(AsyncTestConfig.class)
class SearchCommandListenerTest extends DefaultIntegrationTest {

	@Autowired
	SearchCommandListener sut;


	@BeforeEach
	void setUp() {
		AsyncTestConfig.capturedError.set(null);
		AsyncTestConfig.errorLatch = new CountDownLatch(1);
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
}