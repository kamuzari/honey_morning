package com.sf.honeymorning.domain.brief.entity;

import com.sf.honeymorning.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class TopicModel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_model_id")
    private Long id;

    @JoinColumn(name = "brief_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Brief brief;

    @Column(name = "topic_id", nullable = false)
    private Long topicId;
}
