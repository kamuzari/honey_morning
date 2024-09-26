package com.sf.honeymorning.domain.quiz.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sf.honeymorning.domain.brief.entity.Brief;
import com.sf.honeymorning.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity
@Builder
@AllArgsConstructor
public class Quiz extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Long id;

    @JoinColumn(name = "brief_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
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
