package com.honeymorning.relay.briefing.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.CONTENT_LENGTH;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
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

import com.honeymorning.common.domain.briefing.constraint.QuizConstraint;
import com.honeymorning.relay.briefing.application.domain.TextToSpeechContent;
import com.honeymorning.relay.briefing.application.port.out.CommandBriefingPort;
import com.honeymorning.relay.briefing.application.port.out.CommandContentStorePort;
import com.honeymorning.relay.briefing.application.port.out.CommandTextToSpeechPort;
import com.honeymorning.relay.briefing.application.port.out.LoadBriefingPort;
import com.honeymorning.relay.briefing.application.service.TextToSpeechGenerateService;
import com.honeymorning.relay.context.mock.BriefingMockGenerator;
import com.honeymorning.relay.context.mock.MockTest;

class TextToSpeechCommandUseCaseTest extends MockTest {
	static final String FILE_NAME = "sample-sound.mp3";
	static final String FILE_LOCATION = "./sample/" + FILE_NAME;
	static final ResourceLoader LOADER = new DefaultResourceLoader();

	TextToSpeechCommandUseCase sut;

	@InjectMocks
	TextToSpeechGenerateService sutImpl;

	@Mock
	LoadBriefingPort loadBriefingPort;

	@Mock
	CommandBriefingPort commandBriefingPort;

	@Mock
	CommandTextToSpeechPort commandTextToSpeechPort;

	@Mock
	CommandContentStorePort commandContentStorePort;

	@BeforeEach
	void setUp() {
		this.sut = sutImpl;
	}

	@DisplayName("tts 파일을 만들고 클라우드 업로드 후 변경사항을 반영한다")
	@Test
	void testCreateTts() throws IOException {
		//given
		TextToSpeechContent loadedTextToSpeechContent = new TextToSpeechContent(
			1L,
			BriefingMockGenerator.GENERATOR.lorem().sentence(20),
			Stream.generate(() -> new TextToSpeechContent.textToSpeechQuiz(
					BriefingMockGenerator.GENERATOR.number().randomNumber(),
					BriefingMockGenerator.GENERATOR.lorem().sentence(5)
				)).limit(QuizConstraint.TOTAL_QUIZ_SIZE)
				.toList()
		);

		var expectedTtsFileResponse = getResource();

		given(loadBriefingPort.getTtsBriefingWithQuizzes(loadedTextToSpeechContent.getBriefingId())).willReturn(
			loadedTextToSpeechContent);
		given(commandTextToSpeechPort.create(anyString())).willReturn(expectedTtsFileResponse);
		doNothing().when(commandContentStorePort).upload(anyString(), any(), anyLong(), anyString());

		//when
		sut.create(loadedTextToSpeechContent.getBriefingId());

		//then
		assertThat(loadedTextToSpeechContent.getContent()).isNotNull();
		loadedTextToSpeechContent.getTtsQuizzes().forEach(textToSpeechQuiz ->
			assertThat(textToSpeechQuiz.getContent()).isNotNull()
		);
	}

	ResponseEntity<Resource> getResource() throws IOException {
		Resource resource = LOADER.getResource(FILE_LOCATION);
		HttpHeaders headers = new HttpHeaders();
		headers.set(CONTENT_LENGTH, String.valueOf(resource.getContentAsByteArray().length));
		headers.set(CONTENT_TYPE, "audio/mpeg");

		return new ResponseEntity<>(resource, headers, HttpStatus.CREATED);
	}

}