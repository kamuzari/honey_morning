package com.sf.honeymorning.alarm.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.sf.honeymorning.alarm.client.dto.WakeUpCallSongResponse;
import com.sf.honeymorning.context.IntegrationEnvironment;

@AutoConfigureWireMock(port = 8089)
public class WakeUpCallSongClientTest extends IntegrationEnvironment {

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private WakeUpCallSongClient songClientTest;

	@DisplayName("브리핑 데이터를 응답 받은 후, 이를 기반으로 AI 모닝콜 음악을 만든다")
	@Test
	void testSend() throws JsonProcessingException {
		/// given
		var expectedSongUrl = FAKE_DATA_FACTORY.file().fileName();
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
