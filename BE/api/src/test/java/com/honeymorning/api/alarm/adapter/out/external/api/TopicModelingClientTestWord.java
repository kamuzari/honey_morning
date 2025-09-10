package com.honeymorning.api.alarm.adapter.out.external.api;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.honeymorning.api.brief.utils.BriefingMockGenerator.GENERATOR;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.honeymorning.api.alarm.adapter.out.external.dto.TopicModelDetailResponse;
import com.honeymorning.api.alarm.adapter.out.external.dto.TopicModelingResponse;
import com.honeymorning.api.context.integration.EndPointIntegrationTest;


@AutoConfigureWireMock(port = 8089)
class TopicModelingClientTestWord extends EndPointIntegrationTest {

	static final int FIXED_RESPONSE_SECTION_SIZE = 5;
	static final int FIXED_RESPONSE_DETAIL_WORD_SIZE = 10;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private TopicModelingClient topicModelingClient;

	@DisplayName("브리핑 데이터에서 출현한 주요 관심 키워드들을 가져온다")
	@Test
	void testSend() throws JsonProcessingException {
		/// given
		var expectedResponse = new TopicModelingResponse(Map.of(
			1L, getRandomTopicModelDetails(),
			2L, getRandomTopicModelDetails(),
			3L, getRandomTopicModelDetails(),
			4L, getRandomTopicModelDetails(),
			5L, getRandomTopicModelDetails()
		));
		String expectedBody = objectMapper.writeValueAsString(expectedResponse);

		// when
		WireMock.stubFor(post(urlEqualTo("/ai/topic-model"))
			.willReturn(aResponse()
				.withStatus(OK.value())
				.withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.withBody(expectedBody)
			));

		TopicModelingResponse response = topicModelingClient.send(List.of("정치", "경제"));

		// then
		assertThat(response).isNotNull();
		assertThat(response.sections()).hasSize(FIXED_RESPONSE_SECTION_SIZE);
		response.sections().entrySet()
			.forEach(entry -> assertThat(entry.getValue()).hasSize(FIXED_RESPONSE_DETAIL_WORD_SIZE));
	}

	private List<TopicModelDetailResponse> getRandomTopicModelDetails() {
		return Stream.generate(
				() -> new TopicModelDetailResponse(GENERATOR.lorem().word(),
					GENERATOR.number().randomDouble(100, 0, 100)))
			.limit(FIXED_RESPONSE_DETAIL_WORD_SIZE)
			.toList();
	}
}