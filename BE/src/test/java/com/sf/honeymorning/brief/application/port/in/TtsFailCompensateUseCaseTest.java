package com.sf.honeymorning.brief.application.port.in;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.sf.honeymorning.brief.utils.BriefingMockGenerator;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.event.FailTtsEventEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.event.FailTtsEventEntityRepository;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.BriefingRepository;
import com.sf.honeymorning.common.entity.basic.EventStatus;
import com.sf.honeymorning.config.constant.AwsS3Properties;
import com.sf.honeymorning.context.infra.database.MySqlContext;
import com.sf.honeymorning.context.infra.storage.AwsS3Context;
import com.sf.honeymorning.context.integration.DefaultIntegrationTest;

@AutoConfigureWireMock(port = 8089)
class TtsFailCompensateUseCaseTest extends DefaultIntegrationTest implements MySqlContext, AwsS3Context {
	@Autowired
	TtsFailCompensateUseCase sut;

	@Autowired
	FailTtsEventEntityRepository failTtsEventEntityRepository;

	@Autowired
	BriefingRepository briefingRepository;

	@Value("${aws.s3.bucket-name.tts}")
	String bucketName;

	@Autowired
	AmazonS3 amazonS3Client;

	@Autowired
	AwsS3Properties awsS3Properties;

	@BeforeEach
	void updateUp() throws IOException {
		if (!amazonS3Client.doesBucketExistV2(bucketName)) {
			amazonS3Client.createBucket(
				new CreateBucketRequest(
					bucketName,
					awsS3Properties.region()
				)
			);
		}

		configureExternalTtsMockServer();
	}

	@DisplayName("TTS 콘텐츠 실패 이력을 바탕으로 다시 TTS 콘텐츠를 생성하고 실패이력을 완료로 바꾼다")
	@Test
	void testRetryTts() {
		//given
		var briefingEntity = briefingRepository.save(BriefingMockGenerator.createBriefing(1L));
		var failTtsEventEntity = failTtsEventEntityRepository.save(new FailTtsEventEntity(briefingEntity.getId()));

		//when
		sut.retryTts();

		//then
		var expectRetryCompleted = failTtsEventEntityRepository.findById(failTtsEventEntity.getId()).orElseThrow();
		assertThat(expectRetryCompleted.getEventStatus()).isEqualTo(EventStatus.RETRY_COMPLETED);
	}

	void configureExternalTtsMockServer() throws IOException {
		Resource mockResource = new DefaultResourceLoader()
			.getResource("classpath:/sample/sample-sound.mp3");
		String MOCK_TTS_PATH = "/text-to-speech/XrExE9yKIg1WjnnlVkGX";

		WireMock.stubFor(post(WireMock.urlEqualTo(MOCK_TTS_PATH))
			.willReturn(aResponse()
				.withHeader(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
				.withHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(mockResource.contentLength()))
				.withBody(mockResource.getInputStream().readAllBytes())));
	}
}