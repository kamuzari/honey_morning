package com.sf.honeymorning.quiz.service;

import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.context.MockTestServiceEnvironment;
import com.sf.honeymorning.quiz.common.QuizConstraint;
import com.sf.honeymorning.quiz.controller.dto.SelectionRequestDto;
import com.sf.honeymorning.quiz.domain.entity.Quiz;
import com.sf.honeymorning.quiz.domain.repository.QuizRepository;
import com.sf.honeymorning.quiz.service.mapper.QuizMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class QuizServiceTest extends MockTestServiceEnvironment {
    @InjectMocks
    QuizService sut;

    @Mock
    QuizRepository quizRepository;

    @Mock
    BriefingRepository briefingRepository;

    @Spy
    QuizMapper quizMapper;

    @DisplayName("사용자가 선택한 답안들이 반영된다")
    @Test
    void testAddSelections() {
        //given
        Briefing briefing = new Briefing(AUTH_USER.getId(),
                FAKER_DATE_FACTORY.lorem().sentence(10),
                FAKER_DATE_FACTORY.lorem().sentence(50),
                ""
        );
        List<Quiz> quizzes = LongStream.rangeClosed(1, 2).mapToObj((quizId) -> {
            Quiz quiz = new Quiz(FAKER_DATE_FACTORY.lorem().sentence(3),
                    1,
                    Stream.generate(() -> FAKER_DATE_FACTORY.lorem().word()).limit(QuizConstraint.OPTION_SIZE).toList());
            ReflectionTestUtils.setField(quiz, "id", quizId);
            return quiz;
        }).toList();

        given(briefingRepository.findByUserIdAndId(any(), any())).willReturn(Optional.of(briefing));
        given(quizRepository.findByBriefing(briefing)).willReturn(quizzes);

        var firstSelection = new SelectionRequestDto.SelectionQuizDto(1L, 1);
        var secondSelection = new SelectionRequestDto.SelectionQuizDto(2L, 3);
        var requestDto = new SelectionRequestDto(1L, List.of(firstSelection, secondSelection));

        //when
        sut.addSelections(AUTH_USER.getId(), requestDto);

        //then
        Quiz quizResult1 = quizzes.stream().filter(quiz -> quiz.getId().equals(firstSelection.quizId()))
                .findAny()
                .orElseThrow();
        Quiz quizResult2 = quizzes.stream().filter(quiz -> quiz.getId().equals(secondSelection.quizId()))
                .findAny()
                .orElseThrow();
        assertThat(quizResult1.getSelection()).isEqualTo(firstSelection.selection());
        assertThat(quizResult2.getSelection()).isEqualTo(secondSelection.selection());
    }

    @DisplayName("사용자가 요청한 quizId가 잘못되면 예외가 발생한다")
    @Test
    void failInvalidQuizId() {
        //given
        Briefing briefing = new Briefing(AUTH_USER.getId(),
                FAKER_DATE_FACTORY.lorem().sentence(10),
                FAKER_DATE_FACTORY.lorem().sentence(50),
                ""
        );
        List<Quiz> quizzes = LongStream.rangeClosed(3, 4).mapToObj((quizId) -> {
            Quiz quiz = new Quiz(FAKER_DATE_FACTORY.lorem().sentence(3),
                    1,
                    Stream.generate(() -> FAKER_DATE_FACTORY.lorem().word()).limit(QuizConstraint.OPTION_SIZE).toList());
            ReflectionTestUtils.setField(quiz, "id", quizId);
            return quiz;
        }).toList();

        given(briefingRepository.findByUserIdAndId(any(), any())).willReturn(Optional.of(briefing));
        given(quizRepository.findByBriefing(briefing)).willReturn(quizzes);

        var firstSelection = new SelectionRequestDto.SelectionQuizDto(1L, 1);
        var secondSelection = new SelectionRequestDto.SelectionQuizDto(2L, 3);
        var requestDto = new SelectionRequestDto(1L, List.of(firstSelection, secondSelection));

        //when
        //then
        assertThatThrownBy(() -> sut.addSelections(AUTH_USER.getId(), requestDto))
                .isInstanceOf(BusinessException.class);
    }

    @DisplayName("조회된 퀴즈 사이즈가 2가 아니면 예외가 발생한다")
    @ParameterizedTest(name = "조회된 퀴즈 개수 : {0}")
    @ValueSource(ints = {1, 3, 4})
    void failInvalidQuizzes(int invalidQuizSize) {
        //given
        Briefing briefing = new Briefing(AUTH_USER.getId(),
                FAKER_DATE_FACTORY.lorem().sentence(10),
                FAKER_DATE_FACTORY.lorem().sentence(50),
                ""
        );
        List<Quiz> quizzes = Stream.generate(() -> new Quiz(
                FAKER_DATE_FACTORY.lorem().sentence(3),
                1,
                Stream.generate(() -> FAKER_DATE_FACTORY.lorem().word()).limit(QuizConstraint.OPTION_SIZE).toList()
        )).limit(invalidQuizSize).toList();

        given(briefingRepository.findByUserIdAndId(any(), any())).willReturn(Optional.of(briefing));
        given(quizRepository.findByBriefing(briefing)).willReturn(quizzes);

        var firstSelection = new SelectionRequestDto.SelectionQuizDto(1L, 1);
        var secondSelection = new SelectionRequestDto.SelectionQuizDto(2L, 3);
        var requestDto = new SelectionRequestDto(1L, List.of(firstSelection, secondSelection));

        //when
        //then
        assertThatThrownBy(() -> sut.addSelections(AUTH_USER.getId(), requestDto))
                .isInstanceOf(BusinessException.class);
    }


}