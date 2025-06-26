package com.sf.honeymorning.brief.application.port.in;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.sf.honeymorning.brief.common.TopicWordConstraint.SECTION_MAXIMUM_SIZE;
import static com.sf.honeymorning.brief.common.TopicWordConstraint.SECTION_MINIMUM_SIZE;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

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
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingTagEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.QuizEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.TopicModelWordEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.event.EventStatus;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.event.FailTtsEventEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.event.FailTtsEventEntityRepository;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.BriefingRepository;
import com.sf.honeymorning.config.constant.AwsS3Properties;
import com.sf.honeymorning.context.infra.database.MySqlContext;
import com.sf.honeymorning.context.infra.storage.AwsS3Context;
import com.sf.honeymorning.context.integration.DefaultIntegrationTest;

@AutoConfigureWireMock(port = 8089)
class TtsFailFallbackUseCaseTest extends DefaultIntegrationTest implements MySqlContext, AwsS3Context {

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

	@Autowired
	TtsFailFallbackUseCase sut;

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
		var briefingEntity = briefingRepository.save(createBriefing(1L));
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

	BriefingEntity createBriefing(Long userId) {
		return new BriefingEntity(
			userId,
			DATE_GENERATOR.lorem().sentence(3),
			DATE_GENERATOR.lorem().sentence(3),
			DATE_GENERATOR.internet().url(),
			List.of(new BriefingTagEntity("경제")),
			List.of(
				new QuizEntity(
					DATE_GENERATOR.friends().quote(),
					DATE_GENERATOR.number().numberBetween(1, 4),
					Stream.generate(() -> DATE_GENERATOR.lorem().sentence()).limit(4).toList()
				),
				new QuizEntity(
					DATE_GENERATOR.friends().quote(),
					DATE_GENERATOR.number().numberBetween(1, 4),
					Stream.generate(() -> DATE_GENERATOR.lorem().sentence()).limit(4).toList())
			),
			Stream.generate(() -> new TopicModelWordEntity(
					DATE_GENERATOR.number().numberBetween(SECTION_MINIMUM_SIZE, SECTION_MAXIMUM_SIZE),
					DATE_GENERATOR.lorem().word(),
					DATE_GENERATOR.number().randomDouble(2, 0, 100)))
				.limit(150).toList()
		);
	}
}