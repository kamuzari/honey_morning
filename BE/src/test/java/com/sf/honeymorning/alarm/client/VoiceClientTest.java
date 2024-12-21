package com.sf.honeymorning.alarm.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.sf.honeymorning.alarm.client.dto.VoiceCreateRequestDto;
import com.sf.honeymorning.config.constant.VoiceClientProperties;

@AutoConfigureWireMock(port = 8089)
@SpringBootTest
public class VoiceClientTest {
	static final String SAMPLE_TEXT = """
		현지시간 17일 비트코인이 10만 8천 달러 선을 처음 돌파했습니다.
		미 가상화폐 거래소 코인베이스에 따르면 미 동부 시간 17일 오전 11시 33분(서부 시간 오전 8시 33분) 비트코인 1개당 가격은 24시간 전보다 0.22% 내린 10만 6천734달러(1억 5천339만 원)에 거래됐습니다.
		비트코인 가격은 17일 한때 10만 8천300달러대까지 올랐습니다.
		15일 10만 6천500달러대에 상승한 데 이어 16일에는 10만 7천800달러대까지 오르는 등 3일 연속 신고가입니다.
		같은 시간 시가총액 2위 이더리움은 0.13% 내린 3천947달러, 리플은 3.69% 오른 2.61달러에 거래됐습니다.
		""";
	static final String MOCK_TTS_PATH = "/text-to-speech/XrExE9yKIg1WjnnlVkGX";

	@Autowired
	VoiceClientProperties voiceClientProperties;

	@Autowired
	VoiceClient voiceClient;

	@Test
	@DisplayName("외부 호출을 통해 tts 파일을 만든다")
	void createTts() throws IOException {
		// given
		Resource mockResource = new DefaultResourceLoader()
			.getResource("classpath:/sample/sample-sound.mp3");
		WireMock.stubFor(post(WireMock.urlEqualTo(MOCK_TTS_PATH))
			.willReturn(aResponse()
				.withHeader(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
				.withHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(mockResource.contentLength()))
				.withBody(mockResource.getInputStream().readAllBytes())));
		// when
		ResponseEntity<Resource> resource = voiceClient.createTts(
			voiceClientProperties.path().voiceId(),
			voiceClientProperties.header().xiApiKey(),
			voiceClientProperties.header().optimizeStreamingLatency(),
			voiceClientProperties.header().outputFormat(),
			new VoiceCreateRequestDto(SAMPLE_TEXT, voiceClientProperties));
		// then
		assertThat(resource.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
		assertThat(resource.getBody()).isNotNull();
	}
}
