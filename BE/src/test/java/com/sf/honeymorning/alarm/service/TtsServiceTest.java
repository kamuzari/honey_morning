package com.sf.honeymorning.alarm.service;

import static com.sf.honeymorning.brief.entity.violation.TopicWordViolation.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.HttpHeaders.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sf.honeymorning.alarm.service.client.TtsClientService;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.entity.BriefingTag;
import com.sf.honeymorning.brief.entity.TopicModelWord;
import com.sf.honeymorning.brief.entity.violation.QuizViolation;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.context.MockTestServiceEnvironment;
import com.sf.honeymorning.quiz.domain.entity.Quiz;
import com.sf.honeymorning.quiz.domain.repository.QuizRepository;

class TtsServiceTest extends MockTestServiceEnvironment {

	@InjectMocks
	TtsService ttsService;

	@Mock
	BriefingRepository briefingRepository;

	@Mock
	QuizRepository quizRepository;

	@Mock
	ContentStoreService contentStoreService;

	@Mock
	TtsClientService ttsClientService;

	static final String FILE_NAME = "sample-sound.mp3";
	static final String FILE_LOCATION = "./sample/" + FILE_NAME;
	static final ResourceLoader LOADER = new DefaultResourceLoader();

	@DisplayName("tts 파일을 만들고 클라우드 업로드 후 변경사항을 반영한다")
	@Test
	void testCreateTts() throws IOException {
		//given
		List<Quiz> createdQuizzes = createQuizzes();
		List<TopicModelWord> createdTopicModels = createTopicModelWords();
		Briefing savedBriefing = new Briefing(
			AUTH_USER.getId(),
			FAKER_DATE_FACTORY.lorem().sentence(10),
			FAKER_DATE_FACTORY.lorem().sentence(40),
			FAKER_DATE_FACTORY.internet().domainName(),
			List.of(new BriefingTag(FAKER_DATE_FACTORY.lorem().word())),
			createdQuizzes,
			createdTopicModels
		);

		Resource resource = LOADER.getResource(FILE_LOCATION);
		HttpHeaders headers = new HttpHeaders();
		headers.set(CONTENT_LENGTH, String.valueOf(resource.getContentAsByteArray().length));
		headers.set(CONTENT_TYPE, "audio/mpeg");

		var expectExteriorResponse = new ResponseEntity<>(resource, headers, HttpStatus.CREATED);

		given(briefingRepository.findByIdWithQuizzes(anyLong())).willReturn(Optional.of(savedBriefing));
		given(ttsClientService.create(anyString())).willReturn(expectExteriorResponse);
		doNothing().when(contentStoreService).upload(anyString(), any(), anyLong(), anyString());

		//when
		ttsService.create(1L);

		//then
		assertThat(savedBriefing.getWakeUpBriefingContent()).isNotNull();
		assertThat(savedBriefing.getQuizzes().stream().map(Quiz::getWakeUpQuizContent).toList()).isNotNull();
	}

	@DisplayName("브리핑 데이터가 존재하지 않으면 비즈니스예외가 발생한다")
	@Test
	void failNotExistBriefing() {
		//given
		//when
		//then
		assertThatThrownBy(() -> ttsService.create(anyLong()))
			.isInstanceOf(BusinessException.class);
	}

	@DisplayName("퀴즈 데이터가 존재하지 않으면 비즈니스예외가 발생한다")
	@Test
	void failNotExistQuizzes() throws IOException {
		//given
		List<TopicModelWord> createdTopicModels = createTopicModelWords();
		Briefing savedBriefing = new Briefing(
			AUTH_USER.getId(),
			FAKER_DATE_FACTORY.lorem().sentence(10),
			FAKER_DATE_FACTORY.lorem().sentence(40),
			FAKER_DATE_FACTORY.internet().domainName(),
			List.of(new BriefingTag(FAKER_DATE_FACTORY.lorem().word())),
			null,
			createdTopicModels
		);

		Resource resource = LOADER.getResource(FILE_LOCATION);
		HttpHeaders headers = new HttpHeaders();
		headers.set(CONTENT_LENGTH, String.valueOf(resource.getContentAsByteArray().length));
		headers.set(CONTENT_TYPE, "audio/mpeg");
		var expectExteriorResponse = new ResponseEntity<>(resource, headers, HttpStatus.CREATED);

		given(briefingRepository.findByIdWithQuizzes(anyLong())).willReturn(Optional.of(savedBriefing));
		given(ttsClientService.create(anyString())).willReturn(expectExteriorResponse);
		doNothing().when(contentStoreService).upload(anyString(), any(), anyLong(), anyString());
		//when
		//then
		assertThatThrownBy(() -> ttsService.create(anyLong()))
			.isInstanceOf(BusinessException.class);
	}

	List<Quiz> createQuizzes() {
		return Stream.generate(() -> new Quiz(
				FAKER_DATE_FACTORY.lorem().sentence(2),
				1,
				Stream.generate(() -> FAKER_DATE_FACTORY.lorem().word())
					.limit(QuizViolation.NUMBER_OF_SELECTION)
					.toList()
			)).limit(2)
			.toList();
	}

	List<TopicModelWord> createTopicModelWords() {
		return Stream.generate(() -> new TopicModelWord(
				FAKER_DATE_FACTORY.number().numberBetween(SECTION_MINIMUM_SIZE, SECTION_MAXIMUM_SIZE),
				FAKER_DATE_FACTORY.lorem().word(),
				FAKER_DATE_FACTORY.number().randomDouble(2, 0, 100)))
			.limit(150).toList();
	}
}