package com.sf.honeymorning.brief.integration;

import com.sf.honeymorning.brief.controller.dto.response.detail.QuizResponseDto;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.entity.BriefingTag;
import com.sf.honeymorning.brief.entity.TopicModelWord;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.brief.service.QuizService;
import com.sf.honeymorning.context.integration.DefaultIntegrationTest;
import com.sf.honeymorning.context.infra.database.MySqlContext;
import com.sf.honeymorning.brief.entity.Quiz;
import com.sf.honeymorning.brief.service.mapper.QuizMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Stream;

import static com.sf.honeymorning.brief.common.TopicWordConstraint.SECTION_MAXIMUM_SIZE;
import static com.sf.honeymorning.brief.common.TopicWordConstraint.SECTION_MINIMUM_SIZE;
import static com.sf.honeymorning.brief.common.QuizConstraint.*;
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
                        DATE_GENERATOR.friends().quote(),
                        DATE_GENERATOR.number().numberBetween(1, 4),
                        Stream.generate(() -> DATE_GENERATOR.lorem().sentence()).limit(4).toList()
                ),
                new Quiz(
                        DATE_GENERATOR.friends().quote(),
                        DATE_GENERATOR.number().numberBetween(1, 4),
                        Stream.generate(() -> DATE_GENERATOR.lorem().sentence()).limit(4).toList())
        );
        Briefing briefing = briefingRepository.save(new Briefing(
                userId,
                DATE_GENERATOR.lorem().sentence(3),
                DATE_GENERATOR.lorem().sentence(3),
                DATE_GENERATOR.internet().url(),
                List.of(new BriefingTag("경제")),
                savedQuizzes,
                Stream.generate(() -> new TopicModelWord(
                                DATE_GENERATOR.number().numberBetween(SECTION_MINIMUM_SIZE, SECTION_MAXIMUM_SIZE),
                                DATE_GENERATOR.lorem().word(),
                                DATE_GENERATOR.number().randomDouble(2, 0, 100)))
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
