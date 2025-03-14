package com.sf.honeymorning.brief.service.mapper;

import com.sf.honeymorning.brief.controller.dto.response.detail.QuizResponseDto;
import com.sf.honeymorning.brief.entity.Quiz;
import org.springframework.stereotype.Component;

@Component
public class QuizMapper {
    public QuizResponseDto toQuizResponseDto(Quiz quiz) {
        return new QuizResponseDto(
                quiz.getProblem(),
                quiz.getOption1(),
                quiz.getOption2(),
                quiz.getOption3(),
                quiz.getOption4(),
                quiz.getSelection(),
                quiz.getAnswer()
        );
    }

}
