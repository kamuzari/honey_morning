package com.sf.honeymorning.brief.adapter.out.persistence.mapper;

import com.sf.honeymorning.brief.adapter.in.web.dto.response.detail.QuizResponseDto;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.QuizEntity;
import com.sf.honeymorning.brief.application.domain.EmptySelectionQuiz;

import org.springframework.stereotype.Component;

@Component
public class QuizPersistenceMapper {
    public QuizResponseDto toQuizResponseDto(QuizEntity quizEntity) {
        return new QuizResponseDto(
                quizEntity.getProblem(),
                quizEntity.getOption1(),
                quizEntity.getOption2(),
                quizEntity.getOption3(),
                quizEntity.getOption4(),
                quizEntity.getSelection(),
                quizEntity.getAnswer()
        );
    }

    public EmptySelectionQuiz toEmptySelectionQuiz(QuizEntity quizEntity) {
        return new EmptySelectionQuiz(
            quizEntity.getId(),
            quizEntity.getSelection()
        );
    }
}
