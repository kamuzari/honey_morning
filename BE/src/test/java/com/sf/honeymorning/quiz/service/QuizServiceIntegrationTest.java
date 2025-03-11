package com.sf.honeymorning.quiz.service;

import com.sf.honeymorning.brief.controller.dto.response.detail.QuizResponseDto;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.entity.BriefingTag;
import com.sf.honeymorning.brief.entity.TopicModelWord;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.context.DefaultIntegrationTest;
import com.sf.honeymorning.context.infra.database.MySqlContext;
import com.sf.honeymorning.quiz.common.QuizConstraint;
import com.sf.honeymorning.quiz.domain.entity.Quiz;
import com.sf.honeymorning.quiz.service.mapper.QuizMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Stream;

import static com.sf.honeymorning.brief.entity.violation.TopicWordViolation.SECTION_MAXIMUM_SIZE;
import static com.sf.honeymorning.brief.entity.violation.TopicWordViolation.SECTION_MINIMUM_SIZE;
import static com.sf.honeymorning.quiz.common.QuizConstraint.*;
import static org.assertj.core.api.Assertions.assertThat;

public class QuizServiceIntegrationTest extends DefaultIntegrationTest implements MySqlContext {
    @Autowired
    QuizService sut;

    @Autowired
    BriefingRepository briefingRepository;

    @Autowired
    QuizMapper quizMapper;

    @DisplayName("브리핑에 관련된 퀴즈를 조회한다")
    @Test
    void testGetQuizzes() {
        //given
        Long userId = 1L;
        List<Quiz> savedQuizzes = List.of(
                new Quiz(
                        FAKE_DATA_FACTORY.friends().quote(),
                        FAKE_DATA_FACTORY.number().numberBetween(1, 4),
                        Stream.generate(() -> FAKE_DATA_FACTORY.lorem().sentence()).limit(4).toList()
                ),
                new Quiz(
                        FAKE_DATA_FACTORY.friends().quote(),
                        FAKE_DATA_FACTORY.number().numberBetween(1, 4),
                        Stream.generate(() -> FAKE_DATA_FACTORY.lorem().sentence()).limit(4).toList())
        );
        Briefing briefing = briefingRepository.save(new Briefing(
                userId,
                FAKE_DATA_FACTORY.lorem().sentence(3),
                FAKE_DATA_FACTORY.lorem().sentence(3),
                FAKE_DATA_FACTORY.internet().url(),
                List.of(new BriefingTag("경제")),
                savedQuizzes,
                Stream.generate(() -> new TopicModelWord(
                                FAKE_DATA_FACTORY.number().numberBetween(SECTION_MINIMUM_SIZE, SECTION_MAXIMUM_SIZE),
                                FAKE_DATA_FACTORY.lorem().word(),
                                FAKE_DATA_FACTORY.number().randomDouble(2, 0, 100)))
                        .limit(150).toList()
        ));

        Briefing savedBriefing = briefingRepository.save(briefing);
        var expectQuizResponses = savedQuizzes.stream().map(savedQuiz -> quizMapper.toQuizResponseDto(savedQuiz)).toList();

        //when
        List<QuizResponseDto> quizzes = sut.getQuizzes(userId, savedBriefing.getId());

        //then
        assertThat(quizzes).hasSize(TOTAL_QUIZ_SIZE);
        assertThat(quizzes).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expectQuizResponses);
    }
}
