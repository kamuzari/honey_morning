package com.sf.honeymorning.brief.entity;

import com.sf.honeymorning.common.entity.BaseEntity;
import com.sf.honeymorning.tag.entity.Tag;

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

	@JoinColumn(name = "brief_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Briefing briefing;

	@JoinColumn(name = "tag_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Tag tag;

	public BriefingTag(Briefing briefing, Tag tag) {
		this.briefing = briefing;
		this.tag = tag;
	}

	public BriefingTag(Tag tag) {
		this.tag = tag;
	}
}
