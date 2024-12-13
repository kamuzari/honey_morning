package com.sf.honeymorning.brief.entity;

import com.sf.honeymorning.common.entity.BaseEntity;

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
public class TopicModelWord extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "topic_model_id")
	private Long id;

	@JoinColumn(name = "briefing_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Briefing briefing;

	private int sectionId;

	private String word;

	private Double weight;


	public TopicModelWord(int sectionId, String word, Double weight) {
		this.sectionId = sectionId;
		this.word = word;
		this.weight = weight;
	}
}
