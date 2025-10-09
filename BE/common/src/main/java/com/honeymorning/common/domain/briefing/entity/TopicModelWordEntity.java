package com.honeymorning.common.domain.briefing.entity;


import com.honeymorning.common.common.basic.BaseEntity;

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
public class TopicModelWordEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "topic_model_id")
	private Long id;

	@JoinColumn(name = "briefing_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private BriefingEntity briefingEntity;

	@Column(name = "section_id")
	private int sectionId;

	private String word;

	private Double weight;

	public TopicModelWordEntity(int sectionId, String word, Double weight) {
		this.sectionId = sectionId;
		this.word = word;
		this.weight = weight;
	}

	void addBriefing(BriefingEntity briefingEntity) {
		this.briefingEntity = briefingEntity;
	}
}
