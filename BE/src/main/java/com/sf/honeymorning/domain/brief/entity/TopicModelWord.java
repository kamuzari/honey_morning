package com.sf.honeymorning.domain.brief.entity;

import com.sf.honeymorning.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class TopicModelWord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_model_word_id")
    private Long id;

    @JoinColumn(name = "topic_model_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TopicModel topicModel;

    @JoinColumn(name = "word_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Word word;

    @Column(nullable = false, columnDefinition = "DECIMAL(10, 2) DEFAULT 0.0")
    private Double weight = 0.0;

}

