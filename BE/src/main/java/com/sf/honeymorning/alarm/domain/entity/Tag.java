package com.sf.honeymorning.alarm.domain.entity;

import com.sf.honeymorning.common.entity.basic.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Table(name = "tags")
@Entity
public class Tag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String word;

    protected Tag() {
    }

    public Tag(String word) {
        if (word == null || word.isBlank()) {
            throw new IllegalArgumentException("word of tag object is not blank");
        }

        this.word = word;
    }
}
