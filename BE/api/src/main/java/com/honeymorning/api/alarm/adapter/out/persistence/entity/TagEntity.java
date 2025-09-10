package com.honeymorning.api.alarm.adapter.out.persistence.entity;

import com.honeymorning.common.common.basic.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
