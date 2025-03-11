package com.sf.honeymorning.quiz.domain.entity;

import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.common.entity.basic.BaseEntity;
import com.sf.honeymorning.common.entity.content.Content;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

import static com.sf.honeymorning.quiz.common.QuizConstraint.*;

@Getter
@Entity
public class Quiz extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "briefing_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Briefing briefing;

    @Column(length = 200, nullable = false)
    private String problem;

    @Column(nullable = false)
    private Integer answer;

    @Column(length = 200, nullable = false)
    private String option1;

    @Column(length = 200, nullable = false)
    private String option2;

    @Column(length = 200, nullable = false)
    private String option3;

    @Column(length = 200, nullable = false)
    private String option4;

    private Integer selection;

    @Embedded
    @AttributeOverride(name = "file", column = @Column(name = "access_url"))
    private Content wakeUpQuizContent;

    protected Quiz() {
    }

    public Quiz(String problem,
                int answer,
                List<String> options) {
        if (problem == null || problem.isBlank()) {
            throw new IllegalArgumentException("문제는 null 이거나 공백으로만 이루어질 수 없습니다");
        }

        if(answer < MINIMUM_VALUE || answer > MAXIMUM_VALUE){
            throw new IllegalArgumentException("답안은 [1-4] 이내여야 합니다.");
        }

        if (options.size() != OPTION_SIZE) {
            throw new IllegalArgumentException("객관식은 4지 선다형 입니다.");
        }

        this.problem = problem;
        this.answer = answer;
        this.option1 = options.get(0);
        this.option2 = options.get(1);
        this.option3 = options.get(2);
        this.option4 = options.get(3);
    }

    public void addQuizContent(Content wakeUpQuizContent) {
        this.wakeUpQuizContent = wakeUpQuizContent;
    }

    public void addSelection(Integer selection) {
        if(selection < MINIMUM_VALUE || selection > MAXIMUM_VALUE){
            throw new IllegalArgumentException("선택사항은 [1-4] 번까지만 유효합니다.");
        }

        this.selection = selection;
    }
}
