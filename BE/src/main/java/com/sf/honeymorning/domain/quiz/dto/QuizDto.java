package com.sf.honeymorning.domain.quiz.dto;

import com.sf.honeymorning.domain.brief.entity.Brief;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class QuizDto {
    private Long id;
    private Brief brief;
    private String question;
    private Integer answer;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private Integer selection;
}
