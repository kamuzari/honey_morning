package com.honeymorning.api.alarm.adapter.out.external.api;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.honeymorning.api.brief.utils.BriefingMockGenerator.GENERATOR;
import static org.apache.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.sf.honeymorning.alarm.adapter.out.external.dto.WakeUpCallSongResponse;
import com.honeymorning.api.context.integration.EndPointIntegrationTest;

@AutoConfigureWireMock(port = 8089)
public class WakeUpCallSongClientTest extends EndPointIntegrationTest {

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private WakeUpCallSongClient songClientTest;

	@DisplayName("브리핑 데이터를 응답 받은 후, 이를 기반으로 AI 모닝콜 음악을 만든다")
	@Test
	void testSend() throws JsonProcessingException {
		/// given
		var expectedSongUrl = GENERATOR.file().fileName();
		WakeUpCallSongResponse expectedResponse = new WakeUpCallSongResponse(expectedSongUrl);
		String briefingReadContent = "트럼프 당선이후 많은 비트 코인들이 역대 최고치를 찍으며 경제적 ... ";
		String expectedBody = objectMapper.writeValueAsString(expectedResponse);

		// when
		WireMock.stubFor(post(urlEqualTo("/ai/song"))
			.willReturn(aResponse()
				.withStatus(OK.value())
				.withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.withBody(expectedBody)));

		WakeUpCallSongResponse response = songClientTest.send(briefingReadContent);

		// then
		assertThat(response.url()).isEqualTo(expectedSongUrl);
	}
}
