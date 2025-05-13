package com.sf.honeymorning.alarm.adapter.out.persistence.entity;

import com.sf.honeymorning.common.entity.basic.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Table(name = "tags")
@Entity
public class TagEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String word;

    protected TagEntity() {
    }

    public TagEntity(String word) {
        if (word == null || word.isBlank()) {
            throw new IllegalArgumentException("word of tag object is not blank");
        }

        this.word = word;
    }
}
