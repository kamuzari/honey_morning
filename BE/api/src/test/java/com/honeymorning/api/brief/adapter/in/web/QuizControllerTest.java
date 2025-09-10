package com.honeymorning.api.brief.adapter.in.web;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.honeymorning.api.brief.adapter.in.web.dto.request.SelectionRequestDto;
import com.honeymorning.api.brief.adapter.in.web.port.out.QuizQueryPort;
import com.honeymorning.api.brief.application.port.in.QuizCommandUseCase;
import com.honeymorning.api.common.security.authentication.JwtProviderManager;
import com.honeymorning.api.common.security.authentication.constant.JwtProperty;
import com.honeymorning.api.common.security.authentication.helper.JwtTokenGenerator;
import com.honeymorning.api.common.security.authentication.helper.JwtTokenWebExtractor;
import com.honeymorning.api.config.WebSecurityConfig;
import com.honeymorning.api.context.mock.MockControllerTest;
import com.honeymorning.api.user.adapter.in.web.handler.AuthenticateDiscardHandler;
import com.honeymorning.api.user.adapter.in.web.handler.AuthenticateSuccessHandler;


@WebMvcTest({QuizController.class,
	WebSecurityConfig.class,
	JwtProviderManager.class,
	JwtTokenGenerator.class,
	JwtTokenWebExtractor.class,
	AuthenticateSuccessHandler.class,
	AuthenticateDiscardHandler.class,
	JwtProperty.class})
class QuizControllerTest extends MockControllerTest {
	final String URI_PREFIX = "/api/quizzes";

	@MockBean
	QuizCommandUseCase quizCommandUseCase;

	@MockBean
	QuizQueryPort quizQueryPort;

	@Test
	@DisplayName("브리핑에 관련된 퀴즈를 조회한다")
	void testGetQuizzes() throws Exception {
		//given
		Long briefingId = 1L;
		String path = "/" + briefingId;

		//when
		ResultActions perform = mockMvc.perform(get(URI_PREFIX + path)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		perform.andExpect(status().isOk());
		verify(quizQueryPort, times(1)).getQuizzes(AUTH_ID, briefingId);
	}

	@Test
	@DisplayName("사용자가 선택한 퀴즈 답안을 제출한다")
	void testSolve() throws Exception {
		//given
		var firstSelection = new SelectionRequestDto.SelectionQuizDto(1L, 1);
		var secondSelection = new SelectionRequestDto.SelectionQuizDto(2L, 3);
		var requestDto = new SelectionRequestDto(1L, List.of(firstSelection, secondSelection));
		String body = objectMapper.writeValueAsString(requestDto);

		//when
		ResultActions perform = mockMvc.perform(patch(URI_PREFIX)
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
			.andDo(print());

		//then
		perform.andExpect(status().isOk());
		verify(quizCommandUseCase, times(1)).solve(AUTH_ID, requestDto);
	}

	@DisplayName("사용자가 선택한 답안을 제출할때,")
	@Nested
	class AlarmSetValidatorTest {

		@DisplayName("브리핑 아이디가 없으면 예외가 발생한다")
		@Test
		void failInvalidBriefingId() throws Exception {
			//given
			var firstSelection = new SelectionRequestDto.SelectionQuizDto(1L, 1);
			var secondSelection = new SelectionRequestDto.SelectionQuizDto(2L, 3);
			var requestDto = new SelectionRequestDto(null, List.of(firstSelection, secondSelection));
			String body = objectMapper.writeValueAsString(requestDto);

			//when
			//then
			request(body).andExpect(status().is4xxClientError());
		}

		@DisplayName("선택한 답안 없이 제출하면 예외가 발생한다")
		@Test
		void failInvalidSelectionDtos() throws Exception {
			//given
			var requestDto = new SelectionRequestDto(null, List.of());
			String body = objectMapper.writeValueAsString(requestDto);

			//when
			//then
			request(body).andExpect(status().is4xxClientError());
		}

		@DisplayName("선택한 답안의 개수가 2개가 아니면 예외가 발생한다")
		@ParameterizedTest(name = "size : {0}")
		@ValueSource(ints = {0, 1, 3})
		void failInvalidSelectionDtoSize(int invalidSize) throws Exception {
			//given
			var selectionQuizDtos = Stream.generate(() -> new SelectionRequestDto.SelectionQuizDto(1L, 1))
				.limit(invalidSize)
				.toList();
			var requestDto = new SelectionRequestDto(null, selectionQuizDtos);
			String body = objectMapper.writeValueAsString(requestDto);

			//when
			//then
			request(body).andExpect(status().is4xxClientError());
		}

		private ResultActions request(String body) throws Exception {
			return mockMvc.perform(patch(URI_PREFIX)
					.contentType(MediaType.APPLICATION_JSON)
					.content(body))
				.andDo(print());
		}
	}

}