package com.honeymorning.relay.briefing.adapter.in.consumer;

import static com.honeymorning.common.domain.briefing.constraint.QuizConstraint.TOTAL_QUIZ_SIZE;
import static com.honeymorning.common.domain.briefing.constraint.TopicWordConstraint.SECTION_MAXIMUM_SIZE;
import static com.honeymorning.common.domain.briefing.constraint.TopicWordConstraint.SECTION_MINIMUM_SIZE;
import static com.honeymorning.common.domain.briefing.constraint.TopicWordConstraint.TOPIC_WORD_TOTAL_SIZE;
import static com.honeymorning.relay.context.mock.BriefingMockGenerator.GENERATOR;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.atLeastOnce;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeymorning.common.domain.briefing.constraint.QuizConstraint;
import com.honeymorning.relay.briefing.application.service.AlarmContentService;
import com.honeymorning.relay.briefing.application.service.TextToSpeechGenerateService;
import com.honeymorning.relay.briefing.application.service.dto.AiBriefingDto;
import com.honeymorning.relay.briefing.application.service.dto.AiQuizDto;
import com.honeymorning.relay.briefing.application.service.dto.AiResponseDto;
import com.honeymorning.relay.briefing.application.service.dto.AiTopicDto;
import com.honeymorning.relay.context.integration.DefaultIntegrationTest;

@TestPropertySource(properties = {
	"app.kafka.consumers.ai-store.group-id=${random.uuid}",
	"app.kafka.consumers.ai-briefing-tts.group-id=${random.uuid}",
	"app.kafka.consumers.ai-quiz1-tts.group-id=${random.uuid}",
	"app.kafka.consumers.ai-quiz2-tts.group-id=${random.uuid}"
})
class AiClientConsumerMockTest extends DefaultIntegrationTest {

	@MockitoSpyBean
	AiClientConsumer sut;

	@MockitoSpyBean
	AiClientDltConsumer subSut;

	@MockitoBean
	AlarmContentService alarmContentService;

	@MockitoBean
	TextToSpeechGenerateService textToSpeechGenerateService;

	@Value("${app.kafka.topics.from-ai-store.name}")
	String aiStoreTopic;

	@Value("${app.kafka.topics.from-ai-briefing-tts.name}")
	String briefingTtsTopic;

	@Value("${app.kafka.topics.from-ai-quiz1-tts.name}")
	String quizTts1Topic;

	@Value("${app.kafka.topics.from-ai-quiz2-tts.name}")
	String quizTts2Topic;

	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	@DisplayName("AI 응답 메시지를 소비하여 DB에 저장한다")
	void consumeToStoreDb() throws JsonProcessingException {
		// given
		AiResponseDto response = createAiResponseDto();
		String data = objectMapper.writeValueAsString(response);
		willDoNothing().given(alarmContentService).create(any());

		// when
		kafkaTemplate.send(aiStoreTopic, data);

		// then

		await().atMost(10, SECONDS)
			.untilAsserted(() -> {
					then(sut).should(atLeastOnce()).storeAiResponse(response);
				}
			);
	}

	@Test
	@DisplayName("AI 응답 메시지를 소비하여 브리핑 tts 를 생성한다")
	void consumeToCreateTtsBriefing() throws JsonProcessingException {
		// given
		AiResponseDto response = createAiResponseDto();
		String data = objectMapper.writeValueAsString(response);
		willDoNothing().given(textToSpeechGenerateService).createBriefingTts(any(), any());

		// when
		kafkaTemplate.send(briefingTtsTopic, data);

		// then
		await().atMost(10, SECONDS)
			.untilAsserted(() -> {
					then(sut).should(atLeastOnce()).createBriefingTts(response);
				}
			);
	}

	@Test
	@DisplayName("AI 응답 메시지를 소비하고 예외가 발생되면 2번의 재시도 후 .DLT로 메시지가 전달된다")
	void
	failConsumeToStoreDb() throws JsonProcessingException {
		// given
		AiResponseDto response = createAiResponseDto();
		String data = objectMapper.writeValueAsString(response);
		willThrow(new RuntimeException("DB 저장 중 오류가 발생했습니다.")).given(alarmContentService).create(response);

		// when
		kafkaTemplate.send(aiStoreTopic, data);

		// then
		await().atMost(10, SECONDS)
			.untilAsserted(() -> {
				then(sut).should(atLeastOnce()).storeAiResponse(response);
				then(subSut).should(atLeastOnce()).storeAiResponse(response);
			});
	}

	@Test
	@DisplayName("AI 응답 메시지를 소비하여 1번째 퀴즈 tts 를 생성한다")
	void consumeToCreateQuiz1Tts() throws JsonProcessingException {
		// given
		AiResponseDto response = createAiResponseDto();
		String data = objectMapper.writeValueAsString(response);
		willDoNothing().given(textToSpeechGenerateService).createQuizTts(any(), any(), any());

		// when
		kafkaTemplate.send(quizTts1Topic, data);

		// then
		await().atMost(10, SECONDS)
			.untilAsserted(() -> {
					then(sut).should(atLeastOnce()).createQuiz1Tts(response);
				}
			);
	}

	@Test
	@DisplayName("AI 응답 메시지를 소비하여 2번째 퀴즈 tts 를 생성한다")
	void consumeToCreateQuiz2Tts() throws JsonProcessingException {
		// given
		AiResponseDto response = createAiResponseDto();
		String data = objectMapper.writeValueAsString(response);
		willDoNothing().given(textToSpeechGenerateService).createQuizTts(any(), any(), any());

		// when
		kafkaTemplate.send(quizTts2Topic, data);

		// then
		await().atMost(10, SECONDS)
			.untilAsserted(() -> {
					then(sut).should(atLeastOnce()).createQuiz2Tts(response);
				}
			);
	}

	private AiResponseDto createAiResponseDto() {
		return new AiResponseDto(
			1L,
			new AiBriefingDto(
				GENERATOR.lorem().sentence(10),
				GENERATOR.lorem().sentence(40)
			),
			createFakeQuizDtos(TOTAL_QUIZ_SIZE),
			createFakeAiTopicDtos(TOPIC_WORD_TOTAL_SIZE),
			List.of("정치"),
			"https://cdn.example.com/wakeup.mp3"
		);
	}

	private List<AiTopicDto> createFakeAiTopicDtos(int size) {
		return Stream.generate(() -> new AiTopicDto(
				GENERATOR.number().numberBetween(SECTION_MINIMUM_SIZE, SECTION_MAXIMUM_SIZE),
				GENERATOR.lorem().word(),
				GENERATOR.number().randomDouble(2, 0, 100)))
			.limit(size).toList();
	}

	private List<AiQuizDto> createFakeQuizDtos(int size) {
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