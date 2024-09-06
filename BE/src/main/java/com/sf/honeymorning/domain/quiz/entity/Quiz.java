package com.sf.honeymorning.domain.quiz.entity;

import com.sf.honeymorning.domain.brief.entity.Brief;
import com.sf.honeymorning.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Quiz extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Long id;

    @JoinColumn(name = "brief_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Brief brief;

    @Column(length = 200, nullable = false)
    private String question;

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


}
