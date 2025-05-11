package com.sf.honeymorning.brief.adapter.in.event;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.sf.honeymorning.context.integration.DefaultIntegrationTest;

import jakarta.validation.ValidationException;

class TtsGenerateListenerTest extends DefaultIntegrationTest {

	@Autowired
	TtsGenerateListener ttsGenerateListener;

	@DisplayName("브리핑 아이디가 null이거나, 음수이면 validation 예외가 발생한다")
	@ParameterizedTest(name = "problem : {0}")
	@NullSource
	@ValueSource(longs = {-1, -2, 0})
	void testGenerate(Long briefingId) {
		//given
		//when
		Assertions.assertThatThrownBy(() ->
			ttsGenerateListener.generate(briefingId)
		).isInstanceOf(ValidationException.class);
		//then
	}

}