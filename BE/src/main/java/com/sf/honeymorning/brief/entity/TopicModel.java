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
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TopicModel extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "topic_model_id")
	private Long id;

	@JoinColumn(name = "brief_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Briefing briefing;

	private int sectionId;

	@Column(length = 50, nullable = false)
	private String word;

	@Column(nullable = false, columnDefinition = "DECIMAL(20, 18) DEFAULT 0.0")
	private Double weight = 0.0;

	public TopicModel(Briefing briefing, int sectionId) {
		this.briefing = briefing;
		this.sectionId = sectionId;
	}

	public TopicModel(int sectionId, String word, Double weight) {
		this.sectionId = sectionId;
		this.word = word;
		this.weight = weight;
	}

	public Long getId() {
		return id;
	}

	public Briefing getBriefing() {
		return briefing;
	}

	public long getSectionId() {
		return sectionId;
	}
}
