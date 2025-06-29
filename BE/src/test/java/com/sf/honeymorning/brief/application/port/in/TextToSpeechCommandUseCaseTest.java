package com.sf.honeymorning.brief.application.port.in;

import static com.sf.honeymorning.brief.utils.BriefingMockGenerator.GENERATOR;
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

import com.sf.honeymorning.brief.application.domain.TextToSpeechContent;
import com.sf.honeymorning.brief.application.port.out.CommandBriefingPort;
import com.sf.honeymorning.brief.application.port.out.CommandTextToSpeechPort;
import com.sf.honeymorning.brief.application.port.out.CommandContentStorePort;
import com.sf.honeymorning.brief.application.port.out.LoadBriefingPort;
import com.sf.honeymorning.brief.application.service.TextToSpeechGenerateService;
import com.sf.honeymorning.brief.common.QuizConstraint;
import com.sf.honeymorning.context.mock.MockTest;

class TextToSpeechCommandUseCaseTest extends MockTest {
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

	static final String FILE_NAME = "sample-sound.mp3";
	static final String FILE_LOCATION = "./sample/" + FILE_NAME;
	static final ResourceLoader LOADER = new DefaultResourceLoader();

	/**
	 * why?
	 *   - 내가 왜 이런 데이터가 있다고 가정하고 계속 반복적인 일을 하고 있지 .. ?
	 *   - 의도한대로 해당 객체에 데이터가 들어가고 있는지 확인하기 위한거다.
	 *   - 해당 코드 영역 sut 을 호출했을때, 이 외부 요청은 제거하고
	 *   - 해당 영역에서만 내가 의도한대로 객체가 값을 변경하고 넣고 빼고 업데이트 하는 일련의 행위가 잘 되는지
	 *   - 그랬을때, 외부에 의존하지 않고 테스트 하는것이다.
	 *    - 구현체가 바뀌면 너도 바뀐다. 그래서 서로다른 영역은 구현체가 아닌.. interfacce 규격만이 있는 것이다
	 *    - 이게ㅐ 핵사고날의 장점이다.
	 */
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
			GENERATOR.lorem().sentence(20),
			Stream.generate(() -> new TextToSpeechContent.textToSpeechQuiz(
					GENERATOR.number().randomNumber(),
					GENERATOR.lorem().sentence(5)
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