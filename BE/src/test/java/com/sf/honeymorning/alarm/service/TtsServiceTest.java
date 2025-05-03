package com.sf.honeymorning.alarm.service;

import static com.sf.honeymorning.brief.common.TopicWordConstraint.*;
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
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingTagEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.TopicModelWord;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.BriefingRepository;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.context.mock.MockServiceTest;
import com.sf.honeymorning.brief.common.QuizConstraint;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.QuizEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.QuizRepository;

class TtsServiceTest extends MockServiceTest {

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
		List<QuizEntity> createdQuizzes = createQuizzes();
		List<TopicModelWord> createdTopicModels = createTopicModelWords();
		BriefingEntity savedBriefingEntity = new BriefingEntity(
			AUTH_USER_ENTITY.getId(),
			DATE_GENERATOR.lorem().sentence(10),
			DATE_GENERATOR.lorem().sentence(40),
			DATE_GENERATOR.internet().domainName(),
			List.of(new BriefingTagEntity(DATE_GENERATOR.lorem().word())),
			createdQuizzes,
			createdTopicModels
		);

		Resource resource = LOADER.getResource(FILE_LOCATION);
		HttpHeaders headers = new HttpHeaders();
		headers.set(CONTENT_LENGTH, String.valueOf(resource.getContentAsByteArray().length));
		headers.set(CONTENT_TYPE, "audio/mpeg");

		var expectExteriorResponse = new ResponseEntity<>(resource, headers, HttpStatus.CREATED);

		given(briefingRepository.findByIdWithQuizzes(anyLong())).willReturn(Optional.of(savedBriefingEntity));
		given(ttsClientService.create(anyString())).willReturn(expectExteriorResponse);
		doNothing().when(contentStoreService).upload(anyString(), any(), anyLong(), anyString());

		//when
		ttsService.create(1L);

		//then
		assertThat(savedBriefingEntity.getWakeUpBriefingContent()).isNotNull();
		assertThat(savedBriefingEntity.getQuizEntities().stream().map(QuizEntity::getWakeUpQuizContent).toList()).isNotNull();
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
		BriefingEntity savedBriefingEntity = new BriefingEntity(
			AUTH_USER_ENTITY.getId(),
			DATE_GENERATOR.lorem().sentence(10),
			DATE_GENERATOR.lorem().sentence(40),
			DATE_GENERATOR.internet().domainName(),
			List.of(new BriefingTagEntity(DATE_GENERATOR.lorem().word())),
			List.of(),
			createdTopicModels
		);

		Resource resource = LOADER.getResource(FILE_LOCATION);
		HttpHeaders headers = new HttpHeaders();
		headers.set(CONTENT_LENGTH, String.valueOf(resource.getContentAsByteArray().length));
		headers.set(CONTENT_TYPE, "audio/mpeg");
		var expectExteriorResponse = new ResponseEntity<>(resource, headers, HttpStatus.CREATED);

		given(briefingRepository.findByIdWithQuizzes(anyLong())).willReturn(Optional.of(savedBriefingEntity));
		given(ttsClientService.create(anyString())).willReturn(expectExteriorResponse);
		doNothing().when(contentStoreService).upload(anyString(), any(), anyLong(), anyString());
		//when
		//then
		assertThatThrownBy(() -> ttsService.create(anyLong()))
			.isInstanceOf(BusinessException.class);
	}

	List<QuizEntity> createQuizzes() {
		return Stream.generate(() -> new QuizEntity(
				DATE_GENERATOR.lorem().sentence(2),
				1,
				Stream.generate(() -> DATE_GENERATOR.lorem().word())
					.limit(QuizConstraint.OPTION_SIZE)
					.toList()
			)).limit(2)
			.toList();
	}

	List<TopicModelWord> createTopicModelWords() {
		return Stream.generate(() -> new TopicModelWord(
				DATE_GENERATOR.number().numberBetween(SECTION_MINIMUM_SIZE, SECTION_MAXIMUM_SIZE),
				DATE_GENERATOR.lorem().word(),
				DATE_GENERATOR.number().randomDouble(2, 0, 100)))
			.limit(150).toList();
	}
}