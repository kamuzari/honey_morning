package com.sf.honeymorning.alarm.adapter.out.external.api;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.http.HttpHeaders.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.sf.honeymorning.alarm.adapter.out.external.dto.QuizOption;
import com.sf.honeymorning.alarm.adapter.out.external.dto.QuizResponseDto;
import com.sf.honeymorning.context.integration.EndPointIntegrationTest;

@AutoConfigureWireMock(port = 8089)
class QuizGeneratorClientTest extends EndPointIntegrationTest {

	static final int FIXED_QUIZ_SIZE = 2;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private QuizGeneratorClient quizGeneratorClient;

	static List<QuizResponseDto> getExpectedResponse() {
		return List.of(
			new QuizResponseDto(
				DATE_GENERATOR.rockBand().name(),
				List.of(new QuizOption(1, DATE_GENERATOR.internet().emailAddress()),
					new QuizOption(2, DATE_GENERATOR.internet().emailAddress()),
					new QuizOption(3, DATE_GENERATOR.internet().emailAddress()),
					new QuizOption(4, DATE_GENERATOR.internet().emailAddress())),
				1
			),
			new QuizResponseDto(
				DATE_GENERATOR.rockBand().name(),
				List.of(new QuizOption(1, DATE_GENERATOR.name().username()),
					new QuizOption(2, DATE_GENERATOR.name().username()),
					new QuizOption(3, DATE_GENERATOR.name().username()),
					new QuizOption(4, DATE_GENERATOR.name().username())),
				2
			)
		);
	}

	@DisplayName("브리핑 데이터를 응답 받은 후, 이를 기반으로 퀴즈 문제를 만든다 ")
	@Test
	void testSend() throws JsonProcessingException {
		/// given
		List<QuizResponseDto> expectedResponse = getExpectedResponse();
		String briefingReadContent = "트럼프 당선이후 많은 비트 코인들이 역대 최고치를 찍으며 경제적 ... ";
		String expectedBody = objectMapper.writeValueAsString(expectedResponse);

		// when
		WireMock.stubFor(post(urlEqualTo("/ai/quiz"))
			.willReturn(aResponse()
				.withStatus(OK.value())
				.withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.withBody(expectedBody)));

		var response = quizGeneratorClient.send(briefingReadContent);

		// then
		assertThat(response).hasSize(FIXED_QUIZ_SIZE);
	}
}