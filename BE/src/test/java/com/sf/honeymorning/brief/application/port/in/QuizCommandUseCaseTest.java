package com.sf.honeymorning.brief.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.sf.honeymorning.brief.adapter.in.web.dto.request.SelectionRequestDto;
import com.sf.honeymorning.brief.application.domain.EmptySelectionQuiz;
import com.sf.honeymorning.brief.application.port.out.CommandQuizPort;
import com.sf.honeymorning.brief.application.port.out.LoadQuizPort;
import com.sf.honeymorning.brief.application.service.QuizService;
import com.sf.honeymorning.brief.application.service.mapper.QuizServiceMapper;
import com.sf.honeymorning.brief.common.QuizConstraint;
import com.sf.honeymorning.context.mock.MockTest;

class QuizCommandUseCaseTest extends MockTest {
	QuizCommandUseCase sut;

	@InjectMocks
	QuizService quizService;

	@Mock
	CommandQuizPort commandQuizPort;

	@Mock
	LoadQuizPort loadQuizPort;

	@Spy
	QuizServiceMapper quizServiceMapper;

	@BeforeEach
	public void setup() {
		sut = quizService;
	}

	@DisplayName("사용자가 선택한 답안들이 반영된다")
	@Test
	void testAddSelections() {
		//given
		Long userId = 1L;
		var requestDto = new SelectionRequestDto(1L, List.of(
			new SelectionRequestDto.SelectionQuizDto(1L, 1),
			new SelectionRequestDto.SelectionQuizDto(2L, 3))
		);
		var quizIds = requestDto.selectionQuizDtos()
			.stream()
			.map(SelectionRequestDto.SelectionQuizDto::quizId)
			.toList();
		var expectedEmptySelectionQuizzes = requestDto.selectionQuizDtos()
			.stream()
			.map(v -> new EmptySelectionQuiz(v.quizId(), null))
			.toList();

		given(loadQuizPort.getQuizzes(userId, requestDto.briefingId(), quizIds))
			.willReturn(expectedEmptySelectionQuizzes);

		//when
		sut.solve(1L, requestDto);

		//then
		Assertions.assertThat(expectedEmptySelectionQuizzes).hasSize(QuizConstraint.TOTAL_QUIZ_SIZE);
		expectedEmptySelectionQuizzes.forEach(emptySelectionQuiz ->
			assertThat(emptySelectionQuiz.getSelection()).isNotNull()
		);
	}

	@DisplayName("사용자가 요청한 quizId가 잘못되면 예외가 발생한다")
	@Test
	void failInvalidQuizId() {
		//given
		Long userId = 1L;
		var requestDto = new SelectionRequestDto(1L, List.of(
			new SelectionRequestDto.SelectionQuizDto(1L, 1),
			new SelectionRequestDto.SelectionQuizDto(2L, 3))
		);

		var quizIds = requestDto.selectionQuizDtos()
			.stream()
			.map(SelectionRequestDto.SelectionQuizDto::quizId)
			.toList();

		var notMatchEmptySelectionQuizzes = List.of(
			new EmptySelectionQuiz(3L, null),
			new EmptySelectionQuiz(4L, null)
		);

		given(loadQuizPort.getQuizzes(userId, requestDto.briefingId(), quizIds))
			.willReturn(notMatchEmptySelectionQuizzes);
		//when
		assertThatThrownBy(() ->
			sut.solve(1L, requestDto)
		).isInstanceOf(RuntimeException.class);
	}
}