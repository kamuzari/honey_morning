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
	private Brief brief;

	private Long sectionId;

	public TopicModel(Brief brief, Long sectionId) {
		this.brief = brief;
		this.sectionId = sectionId;
	}

	public Long getId() {
		return id;
	}

	public Brief getBrief() {
		return brief;
	}

	public Long getSectionId() {
		return sectionId;
	}
}
