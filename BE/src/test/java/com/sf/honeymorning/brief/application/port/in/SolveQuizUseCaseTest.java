package com.sf.honeymorning.brief.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.sf.honeymorning.brief.adapter.in.web.dto.request.SelectionRequestDto;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.QuizEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.BriefingRepository;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.QuizRepository;
import com.sf.honeymorning.brief.application.service.QuizService;
import com.sf.honeymorning.brief.common.QuizConstraint;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.context.mock.MockTest;

class SolveQuizUseCaseTest extends MockTest {
	SolveQuizUseCase sut;

	@InjectMocks
	QuizService quizService;

	@Mock
	QuizRepository quizRepository;

	@Mock
	BriefingRepository briefingRepository;

	@BeforeEach
	public void setup() {
		sut = quizService;
	}

	@DisplayName("사용자가 선택한 답안들이 반영된다")
	@Test
	void testAddSelections() {
		//given
		BriefingEntity briefingEntity = new BriefingEntity(AUTH_USER_ENTITY.getId(),
			DATE_GENERATOR.lorem().sentence(10),
			DATE_GENERATOR.lorem().sentence(50),
			""
		);
		List<QuizEntity> quizEntities = LongStream.rangeClosed(1, 2).mapToObj((quizId) -> {
			QuizEntity quizEntity = new QuizEntity(DATE_GENERATOR.lorem().sentence(3),
				1,
				Stream.generate(() -> DATE_GENERATOR.lorem().word()).limit(QuizConstraint.OPTION_SIZE).toList());
			ReflectionTestUtils.setField(quizEntity, "id", quizId);
			return quizEntity;
		}).toList();

		given(briefingRepository.findByUserIdAndId(any(), any())).willReturn(Optional.of(briefingEntity));
		given(quizRepository.findByBriefingEntity(briefingEntity)).willReturn(quizEntities);

		var firstSelection = new SelectionRequestDto.SelectionQuizDto(1L, 1);
		var secondSelection = new SelectionRequestDto.SelectionQuizDto(2L, 3);
		var requestDto = new SelectionRequestDto(1L, List.of(firstSelection, secondSelection));

		//when
		sut.solve(AUTH_USER_ENTITY.getId(), requestDto);

		//then
		QuizEntity quizEntityResult1 = quizEntities.stream()
			.filter(quiz -> quiz.getId().equals(firstSelection.quizId()))
			.findAny()
			.orElseThrow();
		QuizEntity quizEntityResult2 = quizEntities.stream()
			.filter(quiz -> quiz.getId().equals(secondSelection.quizId()))
			.findAny()
			.orElseThrow();
		assertThat(quizEntityResult1.getSelection()).isEqualTo(firstSelection.selection());
		assertThat(quizEntityResult2.getSelection()).isEqualTo(secondSelection.selection());
	}

	@DisplayName("사용자가 요청한 quizId가 잘못되면 예외가 발생한다")
	@Test
	void failInvalidQuizId() {
		//given
		BriefingEntity briefingEntity = new BriefingEntity(AUTH_USER_ENTITY.getId(),
			DATE_GENERATOR.lorem().sentence(10),
			DATE_GENERATOR.lorem().sentence(50),
			""
		);
		List<QuizEntity> quizEntities = LongStream.rangeClosed(3, 4).mapToObj((quizId) -> {
			QuizEntity quizEntity = new QuizEntity(DATE_GENERATOR.lorem().sentence(3),
				1,
				Stream.generate(() -> DATE_GENERATOR.lorem().word()).limit(QuizConstraint.OPTION_SIZE).toList());
			ReflectionTestUtils.setField(quizEntity, "id", quizId);
			return quizEntity;
		}).toList();

		given(briefingRepository.findByUserIdAndId(any(), any())).willReturn(Optional.of(briefingEntity));
		given(quizRepository.findByBriefingEntity(briefingEntity)).willReturn(quizEntities);

		var firstSelection = new SelectionRequestDto.SelectionQuizDto(1L, 1);
		var secondSelection = new SelectionRequestDto.SelectionQuizDto(2L, 3);
		var requestDto = new SelectionRequestDto(1L, List.of(firstSelection, secondSelection));

		//when
		//then
		assertThatThrownBy(() -> sut.solve(AUTH_USER_ENTITY.getId(), requestDto))
			.isInstanceOf(BusinessException.class);
	}

	@DisplayName("조회된 퀴즈 사이즈가 2가 아니면 예외가 발생한다")
	@ParameterizedTest(name = "조회된 퀴즈 개수 : {0}")
	@ValueSource(ints = {1, 3, 4})
	void failInvalidQuizzes(int invalidQuizSize) {
		//given
		BriefingEntity briefingEntity = new BriefingEntity(AUTH_USER_ENTITY.getId(),
			DATE_GENERATOR.lorem().sentence(10),
			DATE_GENERATOR.lorem().sentence(50),
			""
		);
		List<QuizEntity> quizEntities = Stream.generate(() -> new QuizEntity(
			DATE_GENERATOR.lorem().sentence(3),
			1,
			Stream.generate(() -> DATE_GENERATOR.lorem().word()).limit(QuizConstraint.OPTION_SIZE).toList()
		)).limit(invalidQuizSize).toList();

		given(briefingRepository.findByUserIdAndId(any(), any())).willReturn(Optional.of(briefingEntity));
		given(quizRepository.findByBriefingEntity(briefingEntity)).willReturn(quizEntities);

		var firstSelection = new SelectionRequestDto.SelectionQuizDto(1L, 1);
		var secondSelection = new SelectionRequestDto.SelectionQuizDto(2L, 3);
		var requestDto = new SelectionRequestDto(1L, List.of(firstSelection, secondSelection));

		//when
		//then
		assertThatThrownBy(() -> sut.solve(AUTH_USER_ENTITY.getId(), requestDto))
			.isInstanceOf(BusinessException.class);
	}

}