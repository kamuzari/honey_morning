package com.honeymorning.api.brief.adapter.out.persistence.entity;

import com.honeymorning.api.common.entity.basic.BaseEntity;

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
public class BriefingTagEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "briefing_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private BriefingEntity briefingEntity;

	private String word;

	public BriefingTagEntity(String word) {
		this.word = word;
	}

	void addBriefing(BriefingEntity briefingEntity) {
		this.briefingEntity = briefingEntity;
	}
}
