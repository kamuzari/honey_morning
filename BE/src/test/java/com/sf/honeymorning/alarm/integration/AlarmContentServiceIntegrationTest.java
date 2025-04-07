package com.sf.honeymorning.alarm.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.sf.honeymorning.brief.common.TopicWordConstraint.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

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
import com.sf.honeymorning.alarm.domain.entity.Alarm;
import com.sf.honeymorning.alarm.domain.entity.DayOfTheWeek;
import com.sf.honeymorning.alarm.domain.repository.AlarmRepository;
import com.sf.honeymorning.alarm.service.AlarmContentService;
import com.sf.honeymorning.alarm.service.TtsService;
import com.sf.honeymorning.alarm.service.dto.response.AiBriefingDto;
import com.sf.honeymorning.alarm.service.dto.response.AiQuizDto;
import com.sf.honeymorning.alarm.service.dto.response.AiResponseDto;
import com.sf.honeymorning.alarm.service.dto.response.AiTopicDto;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.config.constant.AwsS3Properties;
import com.sf.honeymorning.context.integration.DefaultIntegrationTest;
import com.sf.honeymorning.context.infra.database.MySqlContext;
import com.sf.honeymorning.context.infra.storage.AwsS3Context;
import com.sf.honeymorning.brief.common.QuizConstraint;
import com.sf.honeymorning.brief.entity.Quiz;
import com.sf.honeymorning.user.adapter.out.persistence.entity.UserEntity;
import com.sf.honeymorning.user.adapter.out.persistence.entity.UserRole;
import com.sf.honeymorning.user.adapter.out.persistence.repository.UserRepository;

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
	UserRepository userRepository;

	@Autowired
	BriefingRepository briefingRepository;

	@SpyBean
	TtsService ttsService;

	@BeforeEach
	void updateUp() {
		if(!amazonS3Client.doesBucketExistV2(bucketName)){
			amazonS3Client.createBucket(new CreateBucketRequest(
				bucketName, awsS3Properties.region()));
		}
	}

	@DisplayName("AI 로부터 응답받은 데이터를 저장하고, 이벤트를 발행하여 tts 콘텐츠를 만들고 tts 정보를 삽입하여 업데이트 한다")
	@Test
	void testCreateTotalContents() throws IOException {
		//given
		UserEntity userEntity = new UserEntity(DATE_GENERATOR.name().username(),
			DATE_GENERATOR.internet().password(10, 17),
			DATE_GENERATOR.internet().domainName(),
			UserRole.ROLE_USER
		);

		userRepository.save(userEntity);
		Alarm alarm = Alarm.initialize(userEntity.getId());
		alarm.update(LocalTime.now(), DayOfTheWeek.getToday(), 3, 3, true);
		alarmRepository.save(alarm);

		AiResponseDto responseDto = new AiResponseDto(
			userEntity.getId(),
			new AiBriefingDto(DATE_GENERATOR.lorem().sentence(10), DATE_GENERATOR.lorem().sentence(40)),
			createFakeQuizDtos(QuizConstraint.TOTAL_QUIZ_SIZE),
			createFakeAiTopicDtos(TOPIC_WORD_TOTAL_SIZE),
			List.of("정치"),
			"https://cdn.ycloud.com/03jidmmk39d"
		);

		Resource mockResource = new DefaultResourceLoader()
			.getResource("classpath:/sample/sample-sound.mp3");

		WireMock.stubFor(post(WireMock.urlEqualTo(MOCK_TTS_PATH))
			.willReturn(aResponse()
				.withHeader(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
				.withHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(mockResource.contentLength()))
				.withBody(mockResource.getInputStream().readAllBytes())));

		//when
		sut.create(responseDto);

		//then
		Briefing briefing = briefingRepository.findByIdWithQuizzes(userEntity.getId()).orElseThrow();

		assertThat(briefing).isNotNull();
		assertThat(briefing.getBriefingTags()).isNotNull();
		assertThat(briefing.getSummaryText()).isEqualTo(responseDto.aiBriefings().voiceContent());
		assertThat(briefing.getText()).isEqualTo(responseDto.aiBriefings().readContent());
		assertThat(briefing.getWakeUpCallPath()).isEqualTo(responseDto.AiWakeUpCallPath());
		assertThat(briefing.getWakeUpBriefingContent()).isNotNull();
		assertThat(briefing.getQuizzes().stream().map(Quiz::getWakeUpQuizContent).toList()).hasSize(2);
	}

	@DisplayName("이벤트를 발행하고 리스너에서 예외가 나도 일부 데이터는 저장된다")
	@Test
	void testCreateTotalContents2()  {
		//given
		UserEntity userEntity = new UserEntity(DATE_GENERATOR.name().username(),
			DATE_GENERATOR.internet().password(10, 17),
			DATE_GENERATOR.internet().domainName(),
			UserRole.ROLE_USER
		);

		userRepository.save(userEntity);
		Alarm alarm = Alarm.initialize(userEntity.getId());
		alarm.update(LocalTime.now(), DayOfTheWeek.getToday(), 3, 3, true);
		alarmRepository.save(alarm);

		AiResponseDto responseDto = new AiResponseDto(
			userEntity.getId(),
			new AiBriefingDto(DATE_GENERATOR.lorem().sentence(10), DATE_GENERATOR.lorem().sentence(40)),
			createFakeQuizDtos(QuizConstraint.TOTAL_QUIZ_SIZE),
			createFakeAiTopicDtos(TOPIC_WORD_TOTAL_SIZE),
			List.of("정치"),
			"https://cdn.ycloud.com/03jidmmk39d"
		);

		doThrow(new RuntimeException("이벤트를 수신 받은 강제 예외 발생")).when(ttsService).create(anyLong());

		//when
		sut.create(responseDto);

		//then
		Briefing briefing = briefingRepository.findByIdWithQuizzes(userEntity.getId()).orElseThrow();

		assertThat(briefing).isNotNull();
		assertThat(briefing.getBriefingTags()).isNotNull();
		assertThat(briefing.getSummaryText()).isEqualTo(responseDto.aiBriefings().voiceContent());
		assertThat(briefing.getText()).isEqualTo(responseDto.aiBriefings().readContent());
		assertThat(briefing.getWakeUpCallPath()).isEqualTo(responseDto.AiWakeUpCallPath());
		assertThat(briefing.getWakeUpBriefingContent()).isNull();
		briefing.getQuizzes().forEach(quiz -> assertThat(quiz.getWakeUpQuizContent()).isNull());
	}

	List<AiTopicDto> createFakeAiTopicDtos(int size) {
		return Stream.generate(() -> new AiTopicDto(
				DATE_GENERATOR.number().numberBetween(SECTION_MINIMUM_SIZE, SECTION_MAXIMUM_SIZE),
				DATE_GENERATOR.lorem().word(),
				DATE_GENERATOR.number().randomDouble(2, 0, 100)))
			.limit(size).toList();
	}

	List<AiQuizDto> createFakeQuizDtos(int size) {
		return Stream.generate(() -> new AiQuizDto(
				DATE_GENERATOR.lorem().sentence(2),
				1,
				Stream.generate(() -> DATE_GENERATOR.lorem().word())
					.limit(QuizConstraint.OPTION_SIZE)
					.toList()
			))
			.limit(size)
			.toList();
	}
}