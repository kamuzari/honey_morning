package com.sf.honeymorning.brief.entity;

import com.sf.honeymorning.common.entity.basic.BaseEntity;

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
public class BriefingTag extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "briefing_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Briefing briefing;

	private String word;

	public BriefingTag(String word) {
		this.word = word;
	}

	void addBriefing(Briefing briefing) {
		this.briefing = briefing;
	}
}
