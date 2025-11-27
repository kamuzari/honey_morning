package com.honeymorning.relay.briefing.application.port.service;

import static com.honeymorning.relay.context.mock.BriefingMockGenerator.GENERATOR;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.honeymorning.common.domain.alarm.entity.AlarmEntity;
import com.honeymorning.common.domain.alarm.entity.DayOfTheWeek;
import com.honeymorning.common.domain.alarm.repository.AlarmRepository;
import com.honeymorning.common.domain.briefing.constraint.QuizConstraint;
import com.honeymorning.common.domain.briefing.constraint.TopicWordConstraint;
import com.honeymorning.common.domain.briefing.entity.BriefingEntity;
import com.honeymorning.common.domain.briefing.entity.QuizEntity;
import com.honeymorning.common.domain.briefing.repository.BriefingRepository;
import com.honeymorning.relay.briefing.application.service.AlarmContentService;
import com.honeymorning.relay.briefing.application.service.TextToSpeechGenerateService;
import com.honeymorning.relay.briefing.application.service.dto.AiBriefingDto;
import com.honeymorning.relay.briefing.application.service.dto.AiQuizDto;
import com.honeymorning.relay.briefing.application.service.dto.AiResponseDto;
import com.honeymorning.relay.briefing.application.service.dto.AiTopicDto;
import com.honeymorning.relay.config.storage.constant.AwsS3Properties;
import com.honeymorning.relay.context.mock.BriefingMockGenerator;
import com.honeymorning.relay.context.infra.database.MySqlContext;
import com.honeymorning.relay.context.integration.DefaultIntegrationTest;
import com.honeymorning.relay.context.infra.storage.AwsS3Context;

@AutoConfigureWireMock(port = 8089)
class AlarmContentServiceIntegrationTest extends DefaultIntegrationTest implements MySqlContext, AwsS3Context {
	static final String MOCK_TTS_PATH = "/text-to-speech/XrExE9yKIg1WjnnlVkGX";

	@Autowired
	AlarmContentService sut;

	@Value("${aws.s3.bucket-name.tts}")
	String bucketName;

	@Autowired
	AmazonS3 amazonS3Client;

	@Autowired
	AwsS3Properties awsS3Properties;

	@Autowired
	AlarmRepository alarmRepository;

	@Autowired
	BriefingRepository briefingRepository;

	@SpyBean
	TextToSpeechGenerateService ttsGenerateService;

	@BeforeEach
	void initialize() {
		if (!amazonS3Client.doesBucketExistV2(bucketName)) {
			amazonS3Client.createBucket(new CreateBucketRequest(
				bucketName, awsS3Properties.region()));
		}
	}

	@DisplayName("AI 로부터 응답받은 데이터를 저장하고, 이벤트를 발행하여 tts 콘텐츠를 만들고 tts 정보를 삽입하여 업데이트 한다")
	@Test
	void testCreateTotalContents() throws IOException {
		//given
		Long userId = 1L;
		AlarmEntity alarmEntity = AlarmEntity.initialize(userId);
		alarmEntity.update(LocalTime.now(), DayOfTheWeek.getToday(), 3, 3, true);
		alarmRepository.save(alarmEntity);

		AiResponseDto responseDto = new AiResponseDto(
			userId,
			new AiBriefingDto(
				BriefingMockGenerator.GENERATOR.lorem().sentence(10),
				BriefingMockGenerator.GENERATOR.lorem().sentence(40)),
			createFakeQuizDtos(QuizConstraint.TOTAL_QUIZ_SIZE),
			createFakeAiTopicDtos(TopicWordConstraint.TOPIC_WORD_TOTAL_SIZE),
			List.of("정치"),
			"https://cdn.ycloud.com/03jidmmk39d"
		);

		Resource mockResource = new DefaultResourceLoader()
			.getResource("classpath:/sample/sample-sound.mp3");

		WireMock.stubFor(WireMock.post(WireMock.urlEqualTo(MOCK_TTS_PATH))
			.willReturn(WireMock.aResponse()
				.withHeader(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
				.withHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(mockResource.contentLength()))
				.withBody(mockResource.getInputStream().readAllBytes())));

		//when
		sut.create(responseDto);

		//then
		BriefingEntity briefingEntity = briefingRepository.findByIdWithQuizzes(userId).orElseThrow();

		Assertions.assertThat(briefingEntity).isNotNull();
		Assertions.assertThat(briefingEntity.getBriefingTagEntities()).isNotNull();
		Assertions.assertThat(briefingEntity.getSummaryText()).isEqualTo(responseDto.aiBriefings().summaryContent());
		Assertions.assertThat(briefingEntity.getText()).isEqualTo(responseDto.aiBriefings().readContent());
		Assertions.assertThat(briefingEntity.getWakeUpCallPath()).isEqualTo(responseDto.AiWakeUpCallPath());
		Assertions.assertThat(briefingEntity.getQuizEntities().stream().map(QuizEntity::getWakeUpQuizContent).toList())
			.hasSize(2);
	}

	@DisplayName("이벤트를 발행하고 리스너에서 예외가 나도 일부 데이터는 저장된다")
	@Test
	void testCreateTotalContentsNotPropagateError() {
		Long userId = 2L;
		//given
		AlarmEntity alarmEntity = AlarmEntity.initialize(userId);
		alarmEntity.update(LocalTime.now(), DayOfTheWeek.getToday(), 3, 3, true);
		alarmRepository.save(alarmEntity);

		AiResponseDto responseDto = new AiResponseDto(
			userId,
			new AiBriefingDto(
				BriefingMockGenerator.GENERATOR.lorem().sentence(10),
				BriefingMockGenerator.GENERATOR.lorem().sentence(40)),
			createFakeQuizDtos(QuizConstraint.TOTAL_QUIZ_SIZE),
			createFakeAiTopicDtos(TopicWordConstraint.TOPIC_WORD_TOTAL_SIZE),
			List.of("정치"),
			"https://cdn.ycloud.com/03jidmmk39d"
		);

		doThrow(new RuntimeException("이벤트를 수신 받은 강제 예외 발생")).when(ttsGenerateService).create(anyLong());

		//when
		sut.create(responseDto);

		//then
		BriefingEntity briefingEntity = briefingRepository.findByIdWithQuizzes(userId).orElseThrow();

		Assertions.assertThat(briefingEntity).isNotNull();
		Assertions.assertThat(briefingEntity.getBriefingTagEntities()).isNotNull();
		Assertions.assertThat(briefingEntity.getSummaryText()).isEqualTo(responseDto.aiBriefings().summaryContent());
		Assertions.assertThat(briefingEntity.getText()).isEqualTo(responseDto.aiBriefings().readContent());
		Assertions.assertThat(briefingEntity.getWakeUpCallPath()).isEqualTo(responseDto.AiWakeUpCallPath());
		Assertions.assertThat(briefingEntity.getWakeUpBriefingContent()).isNull();
		briefingEntity.getQuizEntities().forEach(quiz -> Assertions.assertThat(quiz.getWakeUpQuizContent()).isNull());
	}

	List<AiTopicDto> createFakeAiTopicDtos(int size) {
		return Stream.generate(() -> new AiTopicDto(
				BriefingMockGenerator.GENERATOR.number()
					.numberBetween(TopicWordConstraint.SECTION_MINIMUM_SIZE, TopicWordConstraint.SECTION_MAXIMUM_SIZE),
				BriefingMockGenerator.GENERATOR.lorem().word(),
				BriefingMockGenerator.GENERATOR.number().randomDouble(2, 0, 100)))
			.limit(size).toList();
	}

	List<AiQuizDto> createFakeQuizDtos(int size) {
		return List.of(
			new AiQuizDto(
				1,
				GENERATOR.lorem().sentence(2),
				1,
				Stream.generate(() -> GENERATOR.lorem().word())
					.limit(QuizConstraint.OPTION_SIZE)
					.toList()
			),
			new AiQuizDto(
				2,
				GENERATOR.lorem().sentence(2),
				4,
				Stream.generate(() -> GENERATOR.lorem().word())
					.limit(QuizConstraint.OPTION_SIZE)
					.toList()
			)
		);
	}
}