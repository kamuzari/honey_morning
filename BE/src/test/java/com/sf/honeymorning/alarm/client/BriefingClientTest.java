package com.sf.honeymorning.alarm.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.sf.honeymorning.alarm.client.dto.BriefingResponse;
import com.sf.honeymorning.context.EndPointIntegrationEnvironment;

@AutoConfigureWireMock(port = 8089)
public class BriefingClientTest extends EndPointIntegrationEnvironment {

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private BriefingClient briefingClient;

	@DisplayName("알람 브리핑에 필요한 데이터를 외부 요청으로 가져온다")
	@Test
	void testSend() throws JsonProcessingException {
		/// given
		var expectedResponse = new BriefingResponse(
			"최근 비트코인이 최대치를 달성하면서 .... ",
			"트럼프 당선이후 많은 비트 코인들이 역대 최고치를 찍으며 경제적 ... "
		);
		String expectedBody = objectMapper.writeValueAsString(expectedResponse);

		// when
		WireMock.stubFor(post(urlEqualTo("/ai/briefing"))
			.willReturn(aResponse()
				.withStatus(OK.value())
				.withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.withBody(expectedBody)));

		BriefingResponse response = briefingClient.send(List.of("정치", "경제"));

		// then
		assertThat(response.voiceContent()).isEqualTo(expectedResponse.voiceContent());
		assertThat(response.readContent()).isEqualTo(expectedResponse.readContent());
	}
}